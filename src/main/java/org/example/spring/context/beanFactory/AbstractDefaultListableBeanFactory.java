package org.example.spring.context.beanFactory;

import org.example.spring.annotation.Autowired;
import org.example.spring.beanAware.BeanClassAware;
import org.example.spring.beanAware.BeanLazyAware;
import org.example.spring.beanAware.BeanNameAware;
import org.example.spring.beanAware.BeanScopeAware;
import org.example.spring.beanPostProcessor.*;
import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.AutowiredConstructorElement;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.proxy.ProxyBeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

public class AbstractDefaultListableBeanFactory implements AbstractFactory {
    final private List<BeanPostProcessor> beanPostProcessors;
    final private DefaultListableBeanFactory registry;
    private Set<String> singletonCurrentlyInCreation = new HashSet<>();

    AbstractDefaultListableBeanFactory(DefaultListableBeanFactory registry) {
        this.beanPostProcessors = registry.getBeanPostProcessors();
        this.registry = registry;
    }


    /// 1.先从一层缓存中拿，拿到了再判断是不是工厂类
    /// 2.在该bean工厂查找有木有这个bd，要是木有就去父工厂找并且创建（父工厂创建也大概率是抽象方法然后再调用该工程的方法创建）
    /// 3.进入创造流程
    /// （1）寻找依赖对象，有就将依赖对象返回第一步（将依赖对象getbean）
    ///  (2) 单例创造
    ///  （3）多态创造
    public Object doGetBean(String name) {

        /// 接下来进入到正常的创建流程
        /// 1. 获取bd
        /// 2.从bd中获取所有依赖
        /// 3.遍历每一个依赖的类的名字，然后把这俩打包在一起，加到map中
        /// 4.调用一次getbean生成但是没拿？（我给扔到二级缓存里了）
        /// 5.判断单例多态，单例就写一个三层缓存的beanFactory。调用getSingleton()
        Object beanInstance = null;
        BeanDefinition bd = registry.getBeanDefinition(name);

        if (bd.isSingleton()) {
            beanInstance = getSingleton(name, () -> {
                try {
                    return createBean(bd);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else if (bd.isPrototype()) {
            try {
                beanInstance = createBean(bd);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return beanInstance;
    }

    @Override
    public Object getSingleton(String name, ObjectFactory<?> singletonFactory) {
        Object singltonObject = registry.getSingleton(name);
        try {
            if (singltonObject == null) {
                singletonCurrentlyInCreation.add(name);
                singltonObject = singletonFactory.getObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return singltonObject;
    }

    @Override
    public Object createBean(BeanDefinition bd) throws Exception {
        /// 进行实例化前的判断
        Object bean = resolveBeforeInstantiation(bd);
        if (bean != null) {
            return bean;
        } else {
            return doCreatBean(bd);
        }
    }

    /// !实际的创造bean并没有这么单调，需要继续添加东西，以解决循环依赖
    private Object doCreatBean(BeanDefinition bd) {
        //实例化
        Object earlyBean = instantiationBean(bd);
        //放入三级缓存
        registry.addFactory(bd.getClassName(), packSingletonFactory(bd.getClassName(), earlyBean));
        //实例化后
        /// 在spring中该接口原话有说该接口为框架内部接口，因此就不调用原本的实例化接口了
        applyMergedBeanDefinitionPostProcessors(bd, earlyBean.getClass(), bd.getClassName());
        //依赖注入
        earlyBean = populateBean(earlyBean, bd);
        /// 在spring中把初始化的三个步骤全都塞一个方法里了，我就不了，拆开吧
        applyBeanAware(earlyBean, bd);
        earlyBean = applyBeanPostProcessorsBeforeInitialization(earlyBean, bd.getClassName());
        /// 虽说是调用多个初始化方法，但是我只提供实现接口的那一个初始化方法，里面也只写了实现接口的逻辑
        try {
            //初始化
            invokeInitMethods(earlyBean, bd);
        } catch (Exception e) {
            throw new RuntimeException("初始化报错" + bd.getClassName());
        }
        //初始化后
        earlyBean = applyBeanPostProcessorsAfterInitialization(earlyBean, bd.getClassName());

        return earlyBean;
    }

    /// 实例化前
    // 我在想我要不要把实现该类的beanPostProcessor先拿出来，但是拿出来也是一个遍历
    private Object resolveBeforeInstantiation(BeanDefinition bd) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof InstantiationAwareBeanPostProcessor ibp) {
                Object bean = ibp.postProcessBeforeInstantiation(bd.getClazz(), bd.getClassName());
                return bean;
            }
        }
        return null;
    }

    /// 实例化
    @Override
    public Object instantiationBean(BeanDefinition bd) {
        Constructor<?>[] constructors = null;
        Object bean = null;
        try {
            constructors = bd.getClazz().getConstructors();
            Constructor<?> theConstructor = null;

            //先判断@Autowired注解数量及其报错情况
            int i = 0, j = 0;
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    i++;
                    Autowired declaredAnnotation = constructor.getDeclaredAnnotation(Autowired.class);
                    if (declaredAnnotation.required()) {
                        j++;
                    }
                }
            }
            if (i != 0) {
                if (j == i || (j == 1 && i >= 2 * j)) {
                    throw new RuntimeException("过多被Autowired注解的构造方法");
                }
            }

            //获取构造方法,没有自定义就给无参，有就给自定义的
            if (!(constructors.length == 1)) throw new RuntimeException("多个构造方法");
            theConstructor = constructors[0];
            if (theConstructor.getParameterCount() == 0) {
                bean = theConstructor.newInstance();
            } else {
                Object[] args = getParameterConstructorArgs(theConstructor, bd);
                bean = theConstructor.newInstance(args);
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return bean;
    }

    /// 实例化后
    /// 一般是用于扫描
    private void applyMergedBeanDefinitionPostProcessors(BeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof MergedBeanDefinitionPostProcessor mbp) {
                mbp.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
            }
        }
    }

    /// 依赖注入
    private Object populateBean(Object target, BeanDefinition tbd) {
        String targetName = target.getClass().getSimpleName();   //0的类名
        Map<AutoElement, Boolean> autoElementMap = tbd.getAutoElementMap(); //0们以及是否被注入

        if (autoElementMap != null) {
            for (Map.Entry<AutoElement, Boolean> autoElement : autoElementMap.entrySet()) { //遍历每一个0
                //先把0解包出来
                Field field = autoElement.getKey().getField(); //把0拆出来
                field.setAccessible(true);
                String oneName = field.getType().getSimpleName(); //1的名字
                Object oneBean = registry.getSingleton(oneName); //把1从单例池中掏出来

                //进入循环依赖的判断
                //1，从一层缓存中拿到，那就正常注入然后结束
                //2.没有拿到进入循环依赖状态
                //3.此处需要判断4种状态 ：
                //（1）是否允许循环依赖
                //（2）是否为单例
                //（3）是否为正在创建
                //（4）是否能在二层缓存中拿到
                //4.如果允许循环依赖，则从三层缓存里去拿工厂然后执行，执行完放入二层缓存中，防止其他的bean再创建一次
                //5.依赖注入
                if (oneBean != null) {
                    try {
                        field.set(target, oneBean);
                        autoElement.setValue(true);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else if (registry.containsBeanDefinition(oneName)) {
                    BeanDefinition oneBd = registry.getBeanDefinition(oneName);
                    if (oneBd.isSingleton() && registry.cyclicDependent ) {
                        oneBean = registry.getEarlyBean(oneName);
                        if (oneBean == null  && singletonCurrentlyInCreation.contains(oneName)) {
                            try {
                                oneBean = registry.getFactory(oneName).getObject();
                                registry.addEarlyBean(oneName, oneBean);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            oneBean = registry.getBean(oneName);
                        }
                    }
                    else if (oneBd.isPrototype()) {
                        if (oneBd.isPrototype()) {
                            //你都多态了，我直接创建一个不就完了
                            oneBean = registry.getBean(oneName);
                        }
                    } else {
                        throw new RuntimeException("不允许循环依赖或"+oneName+"既不是单例也不是多态");
                    }
                    //总之都拿出来了，注入吧
                    try {
                        field.set(target, oneBean);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return target;
    }

    /// 调用beanAware
    private void applyBeanAware(Object bean, BeanDefinition bd) {
        if (bean instanceof BeanNameAware ba) {
            ba.beanNameAware(bd.getClazz().getSimpleName());
        }

        if (bean instanceof BeanClassAware ba) {
            ba.beanClassAware(bd.getClazz());
        }

        if (bean instanceof BeanScopeAware ba) {
            ba.beanScopeAware(bd.getScope());
        }

        if (bean instanceof BeanLazyAware) {
            ((BeanLazyAware) bean).beanLazyAware(bd.isLazy());
        }
    }

    /// 初始化前
    private Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            Object temp = bp.postProcessBeforeInitialization(bean, beanName);
            if (temp != null) {
                bean = temp;
            }
        }
        return bean;
    }

    /// 初始化
    private void invokeInitMethods(Object bean, BeanDefinition bd) throws Exception {
        if (bean instanceof InitializingBean iBean) {
            iBean.afterPropertiesSet();
        }
    }

    /// 初始化后
    private Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (registry.containsEarlyBean(beanName)) {
                if(!(bp instanceof SmartInitializationAwareBeanPostProcessor)) {
                    Object temp = bp.postProcessAfterInitialization(bean, beanName);
                    if (temp != null) {
                        bean = temp;
                    }
                }
            }
            else{
                Object temp = bp.postProcessAfterInitialization(bean, beanName);
                if (temp != null) {
                    bean = temp;
                }
            }
        }
        return bean;
    }


    private ObjectFactory<Object> packSingletonFactory(String name, Object bean) {
        //此处的思路应该就是有对所有的beanPostProcessor进行遍历，然后判断有没有实现提前初始化的接口，进行提前Aop
        return () -> {
            Object target = null;
            for (BeanPostProcessor bp : beanPostProcessors) {
                if (bp instanceof SmartInitializationAwareBeanPostProcessor sbp) {
                    target = sbp.applyAfterInitializationMethod(name, bean);
                }
            }
            return target;
        };
    }


    @Deprecated
    //只有单例bean会有实例化之前
    //多态并不会去执行实例化之前的beanPostProcessor
    //此处写实例化之前的判断，调用creatBean和初始化之后的逻辑
    //该方法留给单例bean用
    //此处将会创造完所有单例bean
    public void creatSingletonBeans(Map<String, BeanDefinition> beandefinitionMap) throws Exception {
        for (Map.Entry<String, BeanDefinition> entry : registry.getBeanDefinitionMap().entrySet()) {
            Object bean = applyBeforeInstantiationAwareBeanPostProcessor(beanPostProcessors, entry.getValue());
            if (bean == null) {
                bean = createBean(entry.getValue());
            }
            bean = applyAfterBeanPostProcessor(beanPostProcessors, bean);
            if (isAutoWiredInject(entry.getValue())) {
                registry.registerSingleton(entry.getKey(), bean);
            } else {
                registry.registerEarlyBean(entry.getKey(), bean);
            }
        }
    }

    @Deprecated
    //判断有没有完全实现依赖注入
    private Boolean isAutoWiredInject(BeanDefinition bd) {
        Map<AutoElement, Boolean> autoElementMap = bd.getAutoElementMap();
        for (Boolean ok : autoElementMap.values()) {
            if (!ok) {
                return false;
            }
        }
        return true;
    }


    @Deprecated
    private Object applyBeforeInstantiationAwareBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, BeanDefinition bd) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessBeforeInitialization(bd.getClass(), bd.getClassName());
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    @Deprecated
    private Object applyBeforeBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, Object bean) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessBeforeInitialization(bean, bean.getClass().getSimpleName());
            //这里是idea提示的，用于判断初始化前的方法有没有实现
            return Objects.requireNonNullElse(temp, bean);
        }
        return bean;
    }

    @Deprecated
    private Object applyAfterBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, Object bean) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessAfterInitialization(bean, bean.getClass().getSimpleName());
            if (temp != null) {
                return temp;
            }
        }
        return bean;
    }


    private Object[] getParameterConstructorArgs(Constructor<?> constructor, BeanDefinition bd) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            //尝试判断参数中是否拥有依赖项
            BeanDefinition beanDefinition = registry.getBeanDefinition(parameters[i].getType().getSimpleName());
            if (beanDefinition != null) {
                Object temp = registry.getEarlyBean(beanDefinition.getClassName());
                if (temp == null) {
                    temp = registry.getSingleton(beanDefinition.getClassName());
                    if (temp == null) {
                        ObjectFactory<Object> factory = new ObjectFactory<Object>() {
                            @Override
                            public Object getObject() throws Exception {
                                Object bean = instantiationBean(beanDefinition);
                                BeanPostProcessor proxyProcessor = registry.getSingleton("ProxyBeanPostProcessor") != null ? (BeanPostProcessor) registry.getSingleton("ProxyBeanPostProcessor") : new ProxyBeanPostProcessor();
                                bean = applyAfterBeanPostProcessor(beanPostProcessors, bean);
                                return bean;
                            }
                        };
                        registry.addFactory(beanDefinition.getClassName(), factory);
                        args[i] = getDefaultValueForPrimitiveType(parameters[i].getType());

                        AutowiredConstructorElement autoElement = new AutowiredConstructorElement();
                        Class<?> clazz = bd.getClazz();
                        autoElement.setConstructor(constructor);
                        try {
                            Field autoField = clazz.getDeclaredField(parameters[i].getType().getSimpleName());
                            autoElement.setField(autoField);
                            bd.addAutoElement(autoElement);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                args[i] = temp;
            }
        }
        return args;
    }


    private Object getDefaultValueForPrimitiveType(Class<?> primitiveType) {
        if (primitiveType == int.class) return 0;
        if (primitiveType == long.class) return 0L;
        if (primitiveType == double.class) return 0.0;
        if (primitiveType == float.class) return 0.0f;
        if (primitiveType == boolean.class) return false;
        if (primitiveType == byte.class) return (byte) 0;
        if (primitiveType == char.class) return '\0';
        if (primitiveType == short.class) return (short) 0;
        return null;
    }
}

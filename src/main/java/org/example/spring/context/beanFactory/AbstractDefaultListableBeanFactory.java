package org.example.spring.context.beanFactory;

import org.example.spring.annotation.Autowired;
import org.example.spring.beanAware.BeanClassAware;
import org.example.spring.beanAware.BeanLazyAware;
import org.example.spring.beanAware.BeanNameAware;
import org.example.spring.beanAware.BeanScopeAware;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.ProxyBeanPostProcessor;
import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.AutowiredConstructorElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AbstractDefaultListableBeanFactory implements AbstractFactory {
    final private List<BeanPostProcessor> beanPostProcessors;
    final private DefaultListableBeanFactory registry;

    AbstractDefaultListableBeanFactory(DefaultListableBeanFactory registry) {
        this.beanPostProcessors = registry.getBeanPostProcessors();
        this.registry = registry;
    }


    @Override
    //此处写实例化，初始化前，初始化
    //该方法留给多态bean用
    //该处只创造一个bean
    //由于无论多态还是单例都会走初始化后的逻辑，所以多态的初始化后进行判断执行
    public Object createBean(BeanDefinition beanDefinition) throws Exception {
        Object earlyBean = instantiationBean(beanDefinition);
        registry.registerEarlyBean(beanDefinition.getClassName(), earlyBean);

        //这里进行依赖注入
        earlyBean = registry.applySmartInitializationAwareBeanPostProcessor(true, earlyBean);

        //此处为使用者编写的
        earlyBean = applyBeforeBeanPostProcessor(beanPostProcessors, earlyBean);

        applyBeanAware(earlyBean, beanDefinition); //有就调用，没有就拉倒，此处为初始化
        if (beanDefinition.getScope().equals("prototype")) {
            earlyBean = applyAfterBeanPostProcessor(beanPostProcessors, earlyBean);
        }
        return earlyBean;
    }

    @Override
    //只有单例bean会有实例化之前
    //多态并不会去执行实例化之前的beanPostProcessor
    //此处写实例化之前的判断，调用creatBean和初始化之后的逻辑
    //该方法留给单例bean用
    //此处将会创造完所有单例bean
    //
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

    int index = 0;

    //这啥？
    @Deprecated
    private Object doCreatSingletonBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = applyBeforeInstantiationAwareBeanPostProcessor(beanPostProcessors, beanDefinition);
        if (bean == null) {
            bean = createBean(beanDefinition);
        }
        bean = applyAfterBeanPostProcessor(beanPostProcessors, bean);
        registry.registerSingleton(beanDefinition.getClassName(), bean);
        return null;
    }

    @Deprecated
    public void creatSingletonbeanss(Map<String, BeanDefinition> beandefinitionMap) {
        if (index == beandefinitionMap.size()) {
            return;
        }

    }

    private void applyBeanAware(Object bean, BeanDefinition bd) {
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).beanNameAware(bd.getClassName().toString());
        }

        if (bean instanceof BeanClassAware) {
            ((BeanClassAware) bean).beanClassAware(bd.getClazz());
        }

        if (bean instanceof BeanScopeAware) {
            ((BeanScopeAware) bean).beanScopeAware(bd.getScope());
        }

        if (bean instanceof BeanLazyAware) {
            ((BeanLazyAware) bean).beanLazyAware(bd.getLazy());
        }


    }

    //实例化流程，内部有简单的判断构造方法
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
                Object[] args = getParameterConstructorArgs(theConstructor,bd);
                bean = theConstructor.newInstance(args);
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return bean;
    }

    private Object applyBeforeInstantiationAwareBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, BeanDefinition bd) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessBeforeInitialization(bd.getClass(), bd.getClassName());
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    private Object applyBeforeBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, Object bean) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessBeforeInitialization(bean, bean.getClass().getSimpleName());
            //这里是idea提示的，用于判断初始化前的方法有没有实现
            return Objects.requireNonNullElse(temp, bean);
        }
        return bean;
    }

    private Object applyAfterBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, Object bean) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessAfterInitialization(bean, bean.getClass().getSimpleName());
            if (temp != null) {
                return temp;
            }
        }
        return bean;
    }

    private Object[] getParameterConstructorArgs(Constructor<?>  constructor , BeanDefinition bd) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];

        for(int i = 0 ; i < parameters.length ; i++){
            //尝试判断参数中是否拥有依赖项
            BeanDefinition beanDefinition = registry.getBeanDefinition(parameters[i].getType().getSimpleName());
            if(beanDefinition != null){
                Object temp = registry.getEarlyBean(beanDefinition.getClassName());
                if(temp == null) {
                    temp = registry.getSingleton(beanDefinition.getClassName());
                    if(temp == null){
                        ObjectFactory<Object> factory = new ObjectFactory<Object>() {
                            @Override
                            public Object getObject(AbstractFactory abstractFactory) throws Exception {
                                Object bean = instantiationBean(beanDefinition);
                                BeanPostProcessor proxyProcessor =registry.getSingleton("ProxyBeanPostProcessor") != null ? (BeanPostProcessor) registry.getSingleton("ProxyBeanPostProcessor") : new ProxyBeanPostProcessor(registry);
                                bean = applyAfterBeanPostProcessor(beanPostProcessors,bean);
                                return bean;
                            }
                        };
                        registry.addFactory(beanDefinition.getClassName(),factory);
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

package org.example.spring.context.BeanFactory;

import org.example.spring.Annotation.Autowired;
import org.example.spring.beanAware.BeanClassAware;
import org.example.spring.beanAware.BeanLazyAware;
import org.example.spring.beanAware.BeanNameAware;
import org.example.spring.beanAware.BeanScopeAware;
import org.example.spring.beanPostProcessor.*;
import org.example.spring.create.CircularDependency;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.*;

public class AbstractDefaultListableBeanFactory implements AbstractFactory {
    final private List<BeanPostProcessor> beanPostProcessors;
    final private DefaultListableBeanFactory factory;

    AbstractDefaultListableBeanFactory(DefaultListableBeanFactory factory){
        this.beanPostProcessors = factory.getBeanPostProcessors();
        this.factory = factory;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException(beanName + "不存在");
        } else {
            Object bean = singletonObjects.get(beanName);
            if (bean == null) {
                //单例
                Object earlyBean = getEarlyBean(beanName);
                if (earlyBean != null) {
                    //这里进依赖循环
                    CircularDependency circularDependency = new CircularDependency();
                    return circularDependency.doGetBean(beanName);
                } else {
                    //多态
                    bean = createBean(beanDefinitionMap.get(beanName));
                }
            }
            return bean;
        }
    }

    @Override
    //此处写实例化，初始化前，初始化
    //该方法留给多态bean用
    //该处只创造一个bean
    //由于无论多态还是单例都会走初始化后的逻辑，所以多态的初始化后进行判断执行
    public Object createBean(BeanDefinition beanDefinition) throws Exception {
        Object earlyBean = instantiationBean(beanDefinition);
        putEarlyBean(beanDefinition.getClassName().toString(), earlyBean);

        //这里进行依赖注入
        earlyBean = factory.applySmartInitializationAwareBeanPostProcessor(true,earlyBean);

        //此处为使用者编写的
        earlyBean = applyBeforeBeanPostProcessor(beanPostProcessors, earlyBean);

        applyBeanAware(earlyBean , beanDefinition); //有就调用，没有就拉倒，此处为初始化
        if(beanDefinition.getScope().equals("prototype")){
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
        for( Map.Entry<String, BeanDefinition> entry: beanDefinitionMap.entrySet()){
            Object bean = applyBeforeInstantiationAwareBeanPostProcessor(beanPostProcessors, entry.getValue());
            if (bean == null) {
                bean = createBean(entry.getValue());
            }
           bean = applyAfterBeanPostProcessor(beanPostProcessors, bean);
            factory.registerSingleton(entry.getKey(), bean);
        }
    }
    int index = 0;

    //zhengzaixie
    private Object doCreatSingletonBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = applyBeforeInstantiationAwareBeanPostProcessor(beanPostProcessors, beanDefinition);
        if( bean == null){
            bean = createBean(beanDefinition);
        }
        bean = applyAfterBeanPostProcessor(beanPostProcessors, bean);
        factory.registerSingleton(beanDefinition.getClassName().toString(), bean);
        return  null;
    }
    public void creatSingletonbeanss(Map< String , BeanDefinition> beandefinitionMap){
        if(index == beandefinitionMap.size()){
            return;
        }

    }

    private void applyBeanAware(Object bean ,BeanDefinition bd){
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
    public static Object instantiationBean(BeanDefinition bd) {
        String className = bd.getClazz().getName();
        Constructor<?>[] constructors = null;
        Object bean = null;
        try {
            constructors =bd.getClazz().getConstructors();
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
            if (constructors.length == 1) {
                theConstructor = constructors[0];
            } else if (constructors.length == 2) {
                theConstructor = constructors[1];
            } else {
                //出现多个构造方法时报错，要么只有一个无参，要么只有一个有参数的
                throw new RuntimeException("多个构造方法");
            }
            bean = theConstructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        
        return bean;
    }

    private Object applyBeforeInstantiationAwareBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, BeanDefinition bd){
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            return ((InstantiationAwareBeanPostProcessor)beanPostProcessor).postProcessBeforeInstantiation(bd.getClazz() , bd.getClassName().toString() );
        }
        return null;
    }
    private Object applyBeforeBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, Object bean){
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object temp = beanPostProcessor.postProcessBeforeInitialization(bean, bean.getClass().getSimpleName());
            //这里是idea提示的，用于判断初始化前的方法有没有实现
            return Objects.requireNonNullElse(temp, bean);
        }
        return bean;
    }
    private Object applyAfterBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors, Object bean){
        for(BeanPostProcessor beanPostProcessor : beanPostProcessors){
            Object temp = beanPostProcessor.postProcessAfterInitialization(bean, bean.getClass().getSimpleName());
            return Objects.requireNonNullElse(temp, bean);
        }
        return bean;
    }



    @Deprecated
    private static void beforeInstantiation(Map<String, BeanDefinition> bdMap) {
        //判断实例化前有一个条件，是否重写了postProcessBeforeInstantiation方法，判断方法是查看返回值，因此想遍历方法
        for (BeanDefinition bd : bdMap.values()) {
            Class<?> clazz = bd.getClazz();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals("postProcessBeforeInstantiation") && method.getReturnType() != void.class) {
                    //完成判断后直接将得到的值放入单例池中
                    try {
                        Object bean = clazz.getDeclaredConstructor().newInstance();
                        Object result = ((InstantiationAwareBeanPostProcessor) bean).postProcessBeforeInstantiation(clazz, bd.getClassName().toString());
                        putEarlyBean(bd.getClassName().toString(), result);
                    } catch (Exception e) {
                        throw new RuntimeException("此处为实例化前" + bd.getClassName() + "出现的问题" + e);
                    }
                }
            }
        }
    }

    @Deprecated
    private static void afterInstantiation(BeanDefinition bd) {
        Object bean = getEarlyBean(bd.getClassName().toString());
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("postProcessAfterInstantiation") && method.getReturnType() != void.class) {
                Object result = null;
                try {
                    result = ((InstantiationAwareBeanPostProcessor) bean).postProcessAfterInstantiation(bean, clazz.getName());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                putEarlyBean(clazz.getName(), result);
            }
        }
    }

    @Deprecated
    private static void afterBeanDefinition(BeanDefinition bd) {
        Object bean = getEarlyBean(bd.getClassName().toString());
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("postProcessMergedBeanDefinition")) {
                ((MergedBeanDefinitionPostProcessor) bean).postProcessMergedBeanDefinition(bd, clazz, clazz.getName());
            }
        }
    }

    @Deprecated
    private static void beforeInitialization(BeanDefinition bd) {
        Object bean = getEarlyBean(bd.getClassName().toString());
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("postProcessBeforeInitialization") && method.getReturnType() != void.class) {
                Object result = null;
                result = ((BeanPostProcessor) bean).postProcessAfterInitialization(bean, clazz.getName());
                putEarlyBean(clazz.getName(), result);
            }
        }


    }

    @Deprecated
    private static void afterInitialization() {
        for (Object targetBean : earlySingletonObjects.values()) {
            BeanPostProcessor processor = new ProxyBeanPostProcessor();
            String name = targetBean.getClass().getSimpleName();
            Object bean = processor.postProcessAfterInitialization(targetBean, name);
            singletonObjects.put(name, bean);
        }
    }




}

package org.example.spring.context.beanFactory;

import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.SmartInitializationAwareBeanPostProcessor;
import org.example.spring.beanPostProcessor.SmartInstantiationAwareBeanPostProcessor;
import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();//用于存储beanDefinition
    Map<String, Object> singletonObjects = new HashMap<>();//一层缓存，存储单例Bean
    Map<String, Object> earlySingletonObjects = new HashMap<>();  //二层缓存
    private BeanFactory parentBeanFactory;//拿来存储父beanFactory
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();//存储beanPostProcessor(内部存储的都是生成好的实例)
    private AbstractDefaultListableBeanFactory abstractFactory;
    ClassLoader SUHANCLASSLOADER;
    private Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();//三级缓存


    //此处除了实现接口以外还要想办法依靠初始化和类的多层调用把生命周期走完，一点一点优化吧

    public DefaultListableBeanFactory() {
    }

    //在经过仔细思考以及查看spring实例后只能将beanPostProcessor的扫描安排在这里
    //我本打算放在初始化方法的无参调用上的
    public void refresh() {
        beanPostProcessorReader(beanDefinitionMap);

        /*---------此处已视为beanDefinition_Map生成完毕，并没有考虑bean拥有父类------------- */
        //此处的想法是进行@Autowired注解的扫描和@Aspect注解的扫描
        applySmartInstantiationBeanPostProcessor(true, null);
        //此处将跳跃到abstractFactory
        abstractFactory = new AbstractDefaultListableBeanFactory(this);
        try {
            abstractFactory.creatSingletonBeans(beanDefinitionMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //之后还得添加@import注解和相关逻辑
    }


    //此处设计是用来对我编写的两个BeanPostProcessor进行调用
    //设想：三个注解扫描的
    public void applySmartInstantiationBeanPostProcessor(Boolean isBefore, BeanDefinition bd) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                if (isBefore) {
                    ((SmartInstantiationAwareBeanPostProcessor) bp).applyBeforeInstantiationMethod();
                } else {
                    try {
                        ((SmartInstantiationAwareBeanPostProcessor) bp).applyAfterInstantiationMethod(bd);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    //设想：依赖注入的
    public Object applySmartInitializationAwareBeanPostProcessor(Boolean isBefore, Object bean) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof SmartInitializationAwareBeanPostProcessor) {
                if (isBefore) {
                    return (((SmartInitializationAwareBeanPostProcessor) bp).applyBeforeInitializationMethod(bean));
                } else {
                    ((SmartInitializationAwareBeanPostProcessor) bp).applyAfterInitializationMethod();
                }
            }
        }
        return bean;
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    public void addAllBeanPostProcessor(List<BeanPostProcessor> beanPostProcessors) {
        this.beanPostProcessors.addAll(beanPostProcessors);
    }

    //从beanDefinition_Map中把每个beanPostProcessor挑出来的
    //此处的beanPostProcessor应该为spring中的FactoryPostProcessor，之后会揪出重构
    public void beanPostProcessorReader(Map<String, BeanDefinition> beanDefinitionMap) {
        for (BeanDefinition bd : beanDefinitionMap.values()) {
            if (BeanPostProcessor.class.isAssignableFrom(bd.getClazz())) {//
                //我的list里放的是实例，所以要创造完然后再改
                try {
                    Object beanPostProcessor = bd.getClazz().getConstructor(DefaultListableBeanFactory.class).newInstance(this);
                    addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException("此处为创造beanPostProcessor错误");
                }
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException(beanName + "不存在");
        }
        Object bean = singletonObjects.get(beanName);
        if (bean == null) {
            bean = earlySingletonObjects.get(beanName);
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            if (bean != null && bd.getScope().equals("singleton")) {
                return doGetBean(beanName);
            } else if (bean == null) {
                bean = abstractFactory.createBean(bd);
            }
        }
        return bean;
    }

    public Object doGetBean(String beanName) {
        Object bean = getEarlyBean(beanName);
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        for (Map.Entry<AutoElement, Boolean> entry : bd.getAutoElementMap().entrySet()) {
            if (!entry.getValue()) {
                Field field = entry.getKey().getField();
                String fieldName = field.getType().getSimpleName();
                ObjectFactory<?> oneObjectFactory = this.singletonFactories.get(fieldName);
                if (oneObjectFactory != null) {
                    try {
                        Object oneBean = oneObjectFactory.getObject(abstractFactory);
                        field.setAccessible(true);
                        field.set(bean, oneBean);
                        removeFactory(beanName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return bean;
    }


    /*------------下面都是一些简单的接口方法实现------------*/
    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        SUHANCLASSLOADER = beanClassLoader;
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    @Override//把beanDefinition添加到Map里面
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void registerAllBeanDefinition(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition bd : beanDefinitionList) {
            registerBeanDefinition(bd.getClassName(), bd);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        beanDefinitionMap.remove(beanName);
    }

    //这里套娃
    @Override
    public <T> T getBean(String benaName, Class<T> clazz) throws Exception {
        return clazz.cast(getBean(benaName));
    }

    @Override
    public Boolean containsBean(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }

    @Override
    public Object getEarlyBean(String beanName) {
        return earlySingletonObjects.get(beanName);
    }

    @Override
    public void registerEarlyBean(String beanName, Object bean) {
        earlySingletonObjects.put(beanName, bean);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return singletonObjects.containsKey(beanName);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    public BeanPostProcessor  getBeanPostProcessor(Class clazz){
        for(BeanPostProcessor bp:beanPostProcessors){
            if(clazz.isAssignableFrom(bp.getClass())){
                return bp;
            }
        }
        return null;
    }

    public void addFactory(String name, ObjectFactory<Object> factory) {
        this.singletonFactories.put(name, factory);
    }

    public void removeFactory(String beanName) {
        singletonFactories.remove(beanName);
    }

    @Override
    public Object getFactory(String beanName) {
        return singletonFactories.get(beanName);
    }
}

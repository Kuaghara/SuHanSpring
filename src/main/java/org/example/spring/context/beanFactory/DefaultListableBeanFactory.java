package org.example.spring.context.beanFactory;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Order;
import org.example.spring.beanFactoryPostProcessor.ConfigurationClassParser;
import org.example.spring.beanFactoryPostProcessor.PostProcessorRegistrationDelegate;
import org.example.spring.beanFactoryPostProcessor.BeanFactoryPostProcessor;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.SmartInitializationAwareBeanPostProcessor;
import org.example.spring.beanPostProcessor.SmartInstantiationAwareBeanPostProcessor;
import org.example.spring.context.reader.AnnotationBeanDefinitionReader;
import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.util.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    /// 存储beanDefinition
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    /// 一层缓存，存储单例Bean
    Map<String, Object> singletonObjects = new HashMap<>();
    /// 二层缓存
    Map<String, Object> earlySingletonObjects = new HashMap<>();
    /// 三层缓存
    private Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();
    /// 拿来存储父beanFactory
    private BeanFactory parentBeanFactory;
    /// 存储beanPostProcessor(内部存储的都是生成好的实例)
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    /// 存储抽象工厂
    private AbstractDefaultListableBeanFactory abstractFactory;
    ClassLoader SUHANCLASSLOADER;

    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public boolean cyclicDependent = true;

    public DefaultListableBeanFactory() {
        abstractFactory = new AbstractDefaultListableBeanFactory(this);
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
    public Object getBean(String beanName) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException(beanName + "不存在");
        }
        Object bean = getSingleton(beanName);
        if (bean == null) {
            bean = earlySingletonObjects.get(beanName);
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            if (bean != null && bd.isSingleton()) {
                return doGetBean(beanName);
            } else if (bean == null) {
                bean = abstractFactory.doGetBean(beanName);
            }
        }
        return bean;
    }

     @Override
    public void preInstantiateSingletons() {
        List<BeanDefinition> bds = new ArrayList<>(beanDefinitionMap.values());
        try{
            // 此处我要添加一个对beanDefinitions的排序
            beanDefinitionSort(bds);
            for (BeanDefinition bd : bds){
                String bdName = bd.getClassName();
                if (bd.isSingleton() && !bd.isLazy()) {
                    registerSingleton(bdName, abstractFactory.doGetBean(bdName));
                    removeEarlyBean(bdName);
                }
            }
        }
        catch (Exception e){
            throw new RuntimeException(e+"此处为工厂创建剩余bean错误");
        }
    }

    private void beanDefinitionSort(List<BeanDefinition> beanDefinitionList) {
        for(int i = 0; i < beanDefinitionList.size(); i++){
            for(int j = i + 1; j < beanDefinitionList.size(); j++){
                BeanDefinition bd1 = beanDefinitionList.get(i);
                BeanDefinition bd2 = beanDefinitionList.get(j);
                List<Annotation> ann1 = AnnotationUtil.getAnnonationsList(bd1,this);
                List<Annotation> ann2 = AnnotationUtil.getAnnonationsList(bd2,this);
                if(AnnotationUtil.listIncludeAnnotation(ann1, Order.class)){
                    if(AnnotationUtil.listIncludeAnnotation(ann2, Order.class)){
                        if(getOrderCount(ann1) < getOrderCount(ann2)){
                           AnnotationUtil.ChangeLocation(beanDefinitionList, i, j);
                        }
                    }
                }
                else {
                    if (AnnotationUtil.listIncludeAnnotation(ann2, Order.class)){
                        AnnotationUtil.ChangeLocation(beanDefinitionList, i, j);
                    }
                }
            }
        }
    }

    private int getOrderCount(List<Annotation> a1){
        for(Annotation ann : a1){
            if(ann instanceof Order){
                return ((Order) ann).value();
            }
        }
        return 0;
    }


    /*------------下面都是一些简单的接口方法实现------------*/

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    @Override
    public void cyclicDependentState(boolean state) {
       cyclicDependent = state;
    }

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
    public <T> T getBean(String beanName, Class<T> clazz)  {
        try {
            return clazz.cast(getBean(beanName));
        } catch (Exception e) {
            System.out.println("无法强制转换获取到的bean");
            throw new RuntimeException(e);
        }
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

    @Override
    public boolean containsEarlyBean(String beanName) {
        return earlySingletonObjects.containsKey(beanName);
    }

    public void addFactory(String name, ObjectFactory<Object> factory) {
        this.singletonFactories.put(name, factory);
    }

    public void removeFactory(String beanName) {
        singletonFactories.remove(beanName);
    }

    @Override
    public ObjectFactory<?> getFactory(String beanName) {
        return singletonFactories.get(beanName);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    @Override
    public Boolean isTypeMatch(String name, Class<?> clazz) {
        Class<?> type = this.beanDefinitionMap.get(name).getClazz();
        if(type == null){
            return false;
        }
        return clazz.isAssignableFrom(type);
    }

    @Override
    public List<String> getBeanNameForType(Class<?> clazz) {
        List<String> beanNameList = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()){
            if(clazz.isAssignableFrom(entry.getValue().getClazz())){
                beanNameList.add(entry.getKey());
            }
        }
        return beanNameList;
    }

    @Override
    public List<String> getBeanDefinitionNames() {
        return new ArrayList<>(beanDefinitionMap.keySet());
    }

    @Override
    public void addEarlyBean(String beanName, Object earlyBean) {
        earlySingletonObjects.put(beanName, earlyBean);
    }

    @Override
    public void removeEarlyBean(String beanName) {
        earlySingletonObjects.remove(beanName);
    }

    /*--------------弃置方法-----------------*/

    @Deprecated
    //在经过仔细思考以及查看spring实例后只能将beanPostProcessor的扫描安排在这里
    //我本打算放在初始化方法的无参调用上的
    public void refresh() {

        invokeBeanFactoryPostProcessors(this);

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

    @Deprecated
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

    @Deprecated
    //设想：依赖注入的
    public Object applySmartInitializationAwareBeanPostProcessor(Boolean isBefore, Object bean) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof SmartInitializationAwareBeanPostProcessor) {
                if (isBefore) {
                    return (((SmartInitializationAwareBeanPostProcessor) bp).applyBeforeInitializationMethod(bean));
                }
            }
        }
        return bean;
    }

    @Deprecated
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
                        Object oneBean = oneObjectFactory.getObject();
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

    @Deprecated
    private void invokeBeanFactoryPostProcessors(DefaultListableBeanFactory factory){
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(factory);
    }
    @Deprecated
    public BeanPostProcessor  getBeanPostProcessor(Class<?> clazz){
        for(BeanPostProcessor bp:beanPostProcessors){
            if(clazz.isAssignableFrom(bp.getClass())){
                return bp;
            }
        }
        return null;
    }
}

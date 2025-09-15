package org.example.spring.context.BeanFactory;

import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.SmartInitializationAwareBeanPostProcessor;
import org.example.spring.beanPostProcessor.SmartInstantiationAwareBeanPostProcessor;
import org.example.spring.create.CircularDependency;
import org.example.spring.create.ObjectFactory;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//我打算把这当作beanFactory的实现了(
//在这打个签名:)
public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    public static Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();//用于存储beanDefinition
    public static Map<String, Object> singletonObjects = new HashMap<>();//一层缓存，存储单例Bean
    public static Map<String, Object> earlySingletonObjects = new HashMap<>();  //二层缓存
    private BeanFactory parentBeanFactory;//拿来存储父beanFactory
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();//存储beanPostProcessor(内部存储的都是生成好的实例)
    private AbstractDefaultListableBeanFactory abstractFactory ;
    public static List<Class<?>> AOP_LIST = new ArrayList<>();//存储被注释@Aspect的类
    public static ClassLoader SUHANCLASSLOADER;


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
            if(bp instanceof SmartInstantiationAwareBeanPostProcessor) {
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
    public Object applySmartInitializationAwareBeanPostProcessor(Boolean isBefore, Object bean){
        for (BeanPostProcessor bp : beanPostProcessors) {
            if(bp instanceof SmartInitializationAwareBeanPostProcessor) {
                if (isBefore) {
                    return(((SmartInitializationAwareBeanPostProcessor) bp).applyBeforeInitializationMethod(bean));
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

    //从beanDefinition_Map中把每个beanPostProcessor挑出来的
    public void beanPostProcessorReader(Map<String, BeanDefinition> beanDefinitionMap) {
        List<String> beanPostProcessorName = new ArrayList<>();
        for (BeanDefinition bd : beanDefinitionMap.values()) {
            if (BeanPostProcessor.class.isAssignableFrom(bd.getClazz())) {//
                //我的list里放的是实例，所以要创造完然后再改
                try {
                    Object beanPostProcessor = bd.getClazz().getConstructor().newInstance();
                    addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
                    beanPostProcessorName.add(bd.getClassName().toString());
                    //removeBeanDefinition(bd.getClassName().toString());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException("此处为创造beanPostProcessor错误");
                }
            }
        }
        for(String name : beanPostProcessorName){
            removeBeanDefinition( name);
        }

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

    //此处把ApplicationContext的搬过来
    @Override
    public Object getBean(String beanName) throws Exception {
        if(!beanDefinitionMap.containsKey(beanName)){
            throw  new RuntimeException(beanName+"不存在");
        }
        //User不应该能在一层缓存中查找到，出现问题
        Object bean = singletonObjects.get(beanName);
        if(bean == null ){
            bean = earlySingletonObjects.get(beanName);
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            if( bean == null&&bd.getScope().equals("singleton")){
                CircularDependency circularDependency = new CircularDependency();
                return circularDependency.doGetBean(beanName);
            }
            else if(bean == null){
                bean = abstractFactory.createBean(bd);
            }
        }
        return bean;
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

    public static Object getEarlyBean(String beanName) {
        return earlySingletonObjects.get(beanName);
    }

    public static void putEarlyBean(String beanName, Object bean) {
        earlySingletonObjects.put(beanName, bean);
    }

    public static Object getSingletonBean(String beanName) {
        return singletonObjects.get(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return singletonObjects.containsKey(beanName);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }


}

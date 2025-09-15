package org.example.spring.create;

import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.InstantiationAwareBeanPostProcessor;
import org.example.spring.beanPostProcessor.MergedBeanDefinitionPostProcessor;
import org.example.spring.beanPostProcessor.ProxyBeanPostProcessor;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Method;
import java.util.Map;

//
//import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.earlySingletonObjects;
//import static org.example.spring.create.CreatBeans.creatSingletonBean;
//import static org.example.spring.create.InjectingBeans.injectingBean;
//@Deprecated
//public class Creates {
//    //public static Map<String , Object> earlySingletonObjects = new HashMap<>();//二级缓存
//    public static void doCreat() {
//
//
//        //此处原思路为扫描完后的相关生命周期，但是在aop处发现问题，必须重新编写
//        //之前为一个个beandefinition循环走完全流程，现在将每个流程分开处理,分别写成独立的方法
//        //实例化前->实例化->实例化后->依赖注入->初始化前(卡莫将依赖注入作为了初始化前的实现，我空置了)->初始化后(aop执行)
//        //实例化前：applyBeforeInstantiation();  postProcessor实现
//        //实例化：creatSingletonBean(beanName, bd)；
//        //实例化后；applyAfterInstantiation(bd);  postProcessor实现
//        //实例化后对beanDefinition的处理  postProcessor实现
//        //依赖注入：injectingBean(beanName)；postProcessor实现
//        //初始化前；beforeInitialization(bd) postProcessor实现
//        //初始化：木有初始化:) 创造一个接口，去执行其中自定义的方法
//        //初始化后：afterInitialization()； postProcessor实现
//
//        try{
//            //实例化前
//            beforeInstantiation(BEANDEFINITION_MAP);
//
//            for(BeanDefinition bd : BEANDEFINITION_MAP.values()){
//                if(!earlySingletonObjects.containsKey(bd.getClassName().toString())){
//                    //实例化
//                    creatSingletonBean(bd);
//                    //实例化后
//                    afterInstantiation(bd);
//                    //实例化后对beanDefinition的操作
//                    afterBeanDefinition(bd);
//                    //依赖注入
//                    injectingBean(bd);
//                    //初始化前
//                    beforeInitialization(bd);
//                }
//            }
//            //初始化后(AOP)
//            afterInitialization();
//
//
//            //实例化后的对beanDefinition的操作
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    public static boolean isRealize(BeanDefinition beanDefinition, Class<?> interfaceClass) {
//
//        Class<?> clazz = beanDefinition.getClazz();
//        return interfaceClass.isAssignableFrom(clazz);
//    }
//
//    private static Object applyPostProcessBeforeInstantiation(BeanDefinition beanDefinition) {
//        try {
//            // 创建接口实现类的临时实例
//            Object instance = beanDefinition.getClazz().getDeclaredConstructor().newInstance();
//
//            return ((InstantiationAwareBeanPostProcessor)instance).postProcessBeforeInstantiation(beanDefinition.getClazz(),(String)beanDefinition.getClassName());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    static void applyPostProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName){
//        Object processorInstance = singletonObjects.get(beanName);
//        // 查找并调用postProcessBeforeInstantiation方法
//        ((MergedBeanDefinitionPostProcessor)processorInstance).postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
//    }
//
//    static Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
//        return ((BeanPostProcessor)bean).postProcessBeforeInitialization(bean, beanName);
//    }
//
//    static Object applyBeanPostProcessorsAfterInitialization(String beanName) {
//        //初始化后
//        //Object targetBean = SINGLETONBEAN_MAP.get(beanName);
//        for(Object targetBean : singletonObjects.values()){
//        BeanPostProcessor processor = new ProxyBeanPostProcessor();
//        return processor.postProcessAfterInitialization(targetBean, beanName);
//        }
//        return null;
//    }
//
//    //此处我需要对所有的beanDefinition进行处理，所有实现初始化前的方法都需要想办法让其跳过后面的所有方法，我想到的只有直接加入到容器中
//    private static void beforeInstantiation(Map<String, BeanDefinition> bdMap){
//        //判断实例化前有一个条件，是否重写了postProcessBeforeInstantiation方法，判断方法是查看返回值，因此想遍历方法
//        for(BeanDefinition bd : bdMap.values()){
//            Class<?> clazz = bd.getClazz();
//            for(Method method : clazz.getDeclaredMethods()){
//                if(method.getName().equals("postProcessBeforeInstantiation") && method.getReturnType() != void.class){
//                    //完成判断后直接将得到的值放入单例池中
//                    try {
//                        Object bean = clazz.getDeclaredConstructor().newInstance();
//                        Object result = ((InstantiationAwareBeanPostProcessor)bean).postProcessBeforeInstantiation(clazz,bd.getClassName().toString());
//                        putEarlyBean(bd.getClassName().toString(),result);
//                    } catch (Exception e) {
//                        throw new RuntimeException("此处为实例化前"+bd.getClassName()+"出现的问题"+e);
//                    }
//                }
//            }
//        }
//    }
//
//    private static void afterInstantiation(BeanDefinition bd){
//            Object bean = getEarlyBean(bd.getClassName().toString());
//            Class<?> clazz = bean.getClass();
//            for(Method method : clazz.getDeclaredMethods()){
//                if(method.getName().equals("postProcessAfterInstantiation") && method.getReturnType() != void.class){
//                    Object result = null;
//                    try {
//                        result = ((InstantiationAwareBeanPostProcessor)bean).postProcessAfterInstantiation(bean,clazz.getName());
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                    putEarlyBean(clazz.getName(),result);
//                }
//            }
//    }
//
//    private static void afterBeanDefinition(BeanDefinition bd){
//        Object bean = getEarlyBean(bd.getClassName().toString());
//        Class<?> clazz = bean.getClass();
//        for(Method method : clazz.getDeclaredMethods()){
//            if(method.getName().equals("postProcessMergedBeanDefinition")){
//                ((MergedBeanDefinitionPostProcessor)bean).postProcessMergedBeanDefinition(bd, clazz, clazz.getName());
//            }
//        }
//    }
//
//    private static void beforeInitialization(BeanDefinition bd){
//        Object bean = getEarlyBean(bd.getClassName().toString());
//        Class<?> clazz = bean.getClass();
//        for(Method method : clazz.getDeclaredMethods()){
//            if(method.getName().equals("postProcessBeforeInitialization") && method.getReturnType() != void.class){
//                Object result = null;
//                result = ((BeanPostProcessor)bean).postProcessAfterInitialization(bean,clazz.getName());
//                putEarlyBean(clazz.getName(),result);
//            }
//        }
//
//
//    }
//
//    private static void afterInitialization(){
//        for(Object targetBean : earlySingletonObjects.values()){
//            BeanPostProcessor processor = new ProxyBeanPostProcessor();
//            String name = targetBean.getClass().getSimpleName();
//            Object bean = processor.postProcessAfterInitialization(targetBean, name);
//            singletonObjects.put(name,bean);
//        }
//    }
//
//    public static Object getEarlyBean(String beanName){
//        return earlySingletonObjects.get(beanName);
//    }
//
//    public static void putEarlyBean(String beanName, Object bean){
//        earlySingletonObjects.put(beanName,bean);
//    }
//}

package org.example.spring.create;

import org.example.spring.Annotation.Bean;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.InstantiationAwareBeanPostProcessor;
import org.example.spring.beanPostProcessor.MergedBeanDefinitionPostProcessor;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.example.spring.SuHanApplication.BEANDEFINITION_MAP;
import static org.example.spring.SuHanApplication.SINGLETONBEAN_MAP;
import static org.example.spring.create.CreatBeans.creatSingletonBean;
import static org.example.spring.create.InjectingBeans.injectingBean;

public class Creates {
    public static void creat() {

        for (Map.Entry<String, BeanDefinition> entry : BEANDEFINITION_MAP.entrySet()) {

            BeanDefinition bd = entry.getValue();
            String beanName = entry.getKey();
            Class<?> beanClass = bd.getClazz();
            Method[] methods = beanClass.getDeclaredMethods();

            int i = 0;
            for (Method method : methods) {
                if(method.getName().equals("postProcessBeforeInstantiation")){
                    i++;
                    break;
                }
            }

            // 如果bean实现了InstantiationAwareBeanPostProcessor接口并且是单例，则在实例化前调用接口方法
            if (i == 1 && "singleton".equals(bd.getScope())) {
                // 调用postProcessBeforeInstantiation方法，可能返回一个提前创建的bean实例
                Object bean = applyPostProcessBeforeInstantiation(bd);

                // 如果返回了非null的bean实例，则使用这个实例而不是创建新的
                if (bean != null) {
                    SINGLETONBEAN_MAP.put(beanName, bean);
                    continue; // 跳过正常的实例化过程
                }
            } else if(bd.getScope().equals("singleton")){

                //实例化
                creatSingletonBean(beanName, bd);

                //实例化后
                Object bean = SINGLETONBEAN_MAP.get(beanName);
                if (isRealize(bd, InstantiationAwareBeanPostProcessor.class)) {
                    try {
                        ((InstantiationAwareBeanPostProcessor)bean).postProcessAfterInstantiation(bean, beanName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                //实例化后的对beandefinition的操作
                if (isRealize(bd, MergedBeanDefinitionPostProcessor.class)) {
                    applyPostProcessMergedBeanDefinition(bd , bd.getClazz() , beanName);
                }

                //依赖注入
                injectingBean(beanName);

                //初始化前
                bean = SINGLETONBEAN_MAP.get(beanName);
                if (isRealize(bd, BeanPostProcessor.class)) {
                    Object processedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
                    if (processedBean != null) {
                        bean = processedBean;
                        SINGLETONBEAN_MAP.put(beanName, bean);
                    }
                }

                //初始化后
                bean = SINGLETONBEAN_MAP.get(beanName);
                if(isRealize(bd, BeanPostProcessor.class)) {
                    Object processedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                    if (processedBean != null) {
                        SINGLETONBEAN_MAP.put(beanName, processedBean);
                    }
                }

                //你问我初始化去哪了？初始化的那个接口我都没实现有啥初始化？

            }

        }


    }

    public static boolean isRealize(BeanDefinition beanDefinition, Class<?> interfaceClass) {

        Class<?> clazz = beanDefinition.getClazz();
        return interfaceClass.isAssignableFrom(clazz);
    }

    private static Object applyPostProcessBeforeInstantiation(BeanDefinition beanDefinition) {
        try {
            // 创建接口实现类的临时实例
            Object instance = beanDefinition.getClazz().getDeclaredConstructor().newInstance();

            return ((InstantiationAwareBeanPostProcessor)instance).postProcessBeforeInstantiation(beanDefinition.getClazz(),(String)beanDefinition.getClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    static void applyPostProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName){
        Object processorInstance = SINGLETONBEAN_MAP.get(beanName);
        // 查找并调用postProcessBeforeInstantiation方法
        ((MergedBeanDefinitionPostProcessor)processorInstance).postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    }
    
    static Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        return ((BeanPostProcessor)bean).postProcessBeforeInitialization(bean, beanName);
    }
    
    static Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        return ((BeanPostProcessor)bean).postProcessAfterInitialization(bean, beanName);
    }
}

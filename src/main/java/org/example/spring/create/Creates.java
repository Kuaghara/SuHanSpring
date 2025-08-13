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

            // 如果bean实现了InstantiationAwareBeanPostProcessor接口并且是单例，则在实例化前调用接口方法
            if (isRealize(beanName, InstantiationAwareBeanPostProcessor.class) && "singleton".equals(bd.getScope())) {
                // 调用postProcessBeforeInstantiation方法，可能返回一个提前创建的bean实例
                Object bean = applyPostProcessBeforeInstantiation(bd , beanClass, beanName);

                // 如果返回了非null的bean实例，则使用这个实例而不是创建新的
                if (bean != null) {
                    SINGLETONBEAN_MAP.put(beanName, bean);
                    continue; // 跳过正常的实例化过程
                }
            } else {

                //实例化
                creatSingletonBean(beanName, bd);

                //实例化后
                Object bean = SINGLETONBEAN_MAP.get(beanName);
                if (isRealize(beanName, InstantiationAwareBeanPostProcessor.class)) {
                    if(!applyPostProcessAfterInstantiation(bd , bean , beanName)){
                        throw new RuntimeException("实例化失败");
                    }
                }

                //实例化后的对beandefinition的操作
                if (isRealize(beanName, MergedBeanDefinitionPostProcessor.class)) {
                    applyPostProcessMergedBeanDefinition(bd , bd.getClazz() , beanName);
                }

                //依赖注入
                injectingBean(entry.getKey());

                //初始化前
                Object processedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
                if (processedBean != null) {
                    bean = processedBean;
                    SINGLETONBEAN_MAP.put(beanName, bean);
                }

                //初始化后
                processedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                if (processedBean != null) {
                    SINGLETONBEAN_MAP.put(beanName, processedBean);
                }

                //你问我初始化去哪了？初始化的那个接口我都没实现有啥初始化？

            }

        }


    }

    public static boolean isRealize(String beanName, Class<?> interfaceClass) {
        BeanDefinition beanDefinition = BEANDEFINITION_MAP.get(beanName);
        Class<?> clazz = beanDefinition.getClazz();
        return interfaceClass.isAssignableFrom(clazz);
    }

    private static Object applyPostProcessBeforeInstantiation(BeanDefinition beanDefinition,Object... args) {
        try {
            // 创建接口实现类的实例
            Object instance = beanDefinition.getClazz().getDeclaredConstructor().newInstance();

            // 查找并调用postProcessBeforeInstantiation方法
            Method method = beanDefinition.getClazz().getMethod("postProcessBeforeInstantiation", Class.class, String.class);
            method.setAccessible(true);
            return method.invoke(instance, beanDefinition.getClazz(), beanDefinition.getClassName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean applyPostProcessAfterInstantiation(BeanDefinition beanDefinition, Object bean , String beanName) {
        try {
            // 查找并调用postProcessAfterInstantiation方法
            // 不要创建新的Object对象，而是使用bean参数
            Method method = beanDefinition.getClazz().getMethod("postProcessAfterInstantiation", Object.class, String.class);
            method.setAccessible(true);
            // 使用bean作为方法调用的对象
            return (boolean) method.invoke(bean, bean, beanName);
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private static void applyPostProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName){
        try {
            Object processorInstance = SINGLETONBEAN_MAP.get(beanName);
            // 查找并调用postProcessBeforeInstantiation方法
            Method method = beanDefinition.getClazz().getMethod("postProcessMergedBeanDefinition", Class.class, String.class);
            method.setAccessible(true);
            method.invoke(processorInstance, beanDefinition, beanType, beanName);
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        Object result = bean;
        // 遍历所有的BeanPostProcessor实例
        for (Map.Entry<String, Object> entry : SINGLETONBEAN_MAP.entrySet()) {
            Object processor = entry.getValue();
            if (processor instanceof BeanPostProcessor) {
                try {
                    // 调用postProcessBeforeInitialization方法
                    Object processedBean = ((BeanPostProcessor) processor).postProcessBeforeInitialization(result, beanName);
                    if (processedBean != null) {
                        result = processedBean;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Bean初始化前处理失败: " + beanName, e);
                }
            }
        }
        return result;
    }
    
    private static Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        Object result = bean;
        // 遍历所有的BeanPostProcessor实例
        for (Map.Entry<String, Object> entry : SINGLETONBEAN_MAP.entrySet()) {
            Object processor = entry.getValue();
            if (processor instanceof BeanPostProcessor) {
                try {
                    // 调用postProcessAfterInitialization方法
                    Object processedBean = ((BeanPostProcessor) processor).postProcessAfterInitialization(result, beanName);
                    if (processedBean != null) {
                        result = processedBean;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Bean初始化后处理失败: " + beanName, e);
                }
            }
        }
        return result;
    }
}

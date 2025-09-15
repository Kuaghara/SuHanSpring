package org.example.spring.beanPostProcessor;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{

    // 实例化之前
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName){
        return null;
    }

    // 实例化之后
    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws Exception{
        return true;
    }

    // 自定义的注解注入
    default Object postProcessPropertyValues(Object bean, String beanName) throws Exception{
        return null;
    }
}

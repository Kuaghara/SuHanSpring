package org.example.spring.beanPostProcessor;

public interface BeanPostProcessor {

    // 初始化前
    default Object postProcessBeforeInitialization(Object bean, String beanName){
        return null;
    }

    //初始化后
    default Object postProcessAfterInitialization(Object bean, String beanName){
        return null;
    }
}

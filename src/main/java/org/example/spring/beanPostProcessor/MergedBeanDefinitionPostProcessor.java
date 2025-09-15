package org.example.spring.beanPostProcessor;

import org.example.spring.informationEntity.BeanDefinition;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor{

    // 实例化后对beanDefinition的处理
    default void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName){}

}

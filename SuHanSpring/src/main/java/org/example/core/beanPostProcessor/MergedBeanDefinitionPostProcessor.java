package org.example.core.beanPostProcessor;

import org.example.core.informationEntity.BeanDefinition;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

    // 实例化后对beanDefinition的处理
    default void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

}

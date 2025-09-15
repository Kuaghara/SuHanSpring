package org.example.spring.context.BeanFactory;

import org.example.spring.informationEntity.BeanDefinition;

public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
    BeanDefinition getBeanDefinition(String beanName);
    boolean containsBeanDefinition(String beanName);
    void removeBeanDefinition(String beanName);
}

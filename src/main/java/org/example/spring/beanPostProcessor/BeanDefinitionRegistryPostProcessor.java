package org.example.spring.beanPostProcessor;

import org.example.spring.context.beanFactory.BeanDefinitionRegistry;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor{
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry);
}

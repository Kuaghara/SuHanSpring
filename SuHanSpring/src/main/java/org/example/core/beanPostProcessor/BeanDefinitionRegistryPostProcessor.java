package org.example.core.beanPostProcessor;

import org.example.core.beanFactoryPostProcessor.BeanFactoryPostProcessor;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry);
}

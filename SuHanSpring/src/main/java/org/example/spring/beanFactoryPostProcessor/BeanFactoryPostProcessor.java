package org.example.spring.beanFactoryPostProcessor;

import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}

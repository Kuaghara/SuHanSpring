package org.example.spring.beanPostProcessor;

import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}

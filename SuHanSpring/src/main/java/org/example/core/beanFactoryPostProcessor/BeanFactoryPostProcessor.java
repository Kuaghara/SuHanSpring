package org.example.core.beanFactoryPostProcessor;

import org.example.core.context.beanFactory.ConfigurableListableBeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}

package org.example.core.context;

import org.example.core.beanFactoryPostProcessor.BeanFactoryPostProcessor;
import org.example.core.context.beanFactory.ConfigurableListableBeanFactory;

public interface ConfigurableApplicationContext extends  ApplicationContext {
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);
    void setClassLoader(ClassLoader classLoader);
    void refresh();
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    void close();
}

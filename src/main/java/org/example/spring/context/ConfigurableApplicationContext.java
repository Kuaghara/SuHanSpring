package org.example.spring.context;

import org.example.spring.beanFactoryPostProcessor.BeanFactoryPostProcessor;
import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;

public interface ConfigurableApplicationContext extends  ApplicationContext {
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);
    void setClassLoader(ClassLoader classLoader);
    void refresh();
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    void close();
}

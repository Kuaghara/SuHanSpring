package org.example.spring.context.reader;

import org.example.spring.context.beanFactory.DefaultListableBeanFactory;

public interface BeanDefinitionReader {
    default void loadBeanDefinitions(Class<?> clazz) {
    }

    ;//对配置类进行扫描

    default void loadBeanDefinitions(String location) {
    }

    ;//对路径进行扫描

}

package org.example.spring.context.beanFactory;

public interface ListableBeanFactory {
    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();
}

package org.example.core.context.beanFactory;

public interface ListableBeanFactory {
    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();
}

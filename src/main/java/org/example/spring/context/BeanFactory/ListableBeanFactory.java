package org.example.spring.context.BeanFactory;

public interface ListableBeanFactory {
    boolean containsBeanDefinition(String beanName);
    int getBeanDefinitionCount();
}

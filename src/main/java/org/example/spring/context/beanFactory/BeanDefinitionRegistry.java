package org.example.spring.context.beanFactory;

import org.example.spring.informationEntity.BeanDefinition;

import java.util.List;
import java.util.Map;

public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    void registerAllBeanDefinition(List<BeanDefinition> beanDefinitionList);

    BeanDefinition getBeanDefinition(String beanName);

    Map<String , BeanDefinition> getBeanDefinitionMap();

    boolean containsBeanDefinition(String beanName);

    void removeBeanDefinition(String beanName);

    List<String> getBeanNameForType(Class<?> type);

    List<String> getBeanDefinitionNames();
}

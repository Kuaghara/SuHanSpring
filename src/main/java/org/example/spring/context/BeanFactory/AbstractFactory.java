package org.example.spring.context.BeanFactory;

import org.example.spring.informationEntity.BeanDefinition;

import java.util.Map;

public interface AbstractFactory {
    Object getBean(String beanName) throws Exception;
    void creatSingletonBeans(Map<String , BeanDefinition> beandefinitionMap) throws Exception;
    Object createBean(BeanDefinition beanDefinition) throws Exception;
}

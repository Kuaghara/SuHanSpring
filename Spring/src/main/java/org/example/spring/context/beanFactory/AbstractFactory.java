package org.example.spring.context.beanFactory;

import org.example.spring.informationEntity.BeanDefinition;

import java.util.Map;

public interface AbstractFactory {

    void creatSingletonBeans(Map<String, BeanDefinition> beandefinitionMap) throws Exception;

    Object createBean(BeanDefinition beanDefinition) throws Exception;

    Object instantiationBean(BeanDefinition bd);

    Object getSingleton(String name , ObjectFactory<?> singletonFactory);
}

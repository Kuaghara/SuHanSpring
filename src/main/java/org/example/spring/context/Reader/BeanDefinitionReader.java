package org.example.spring.context.Reader;

import org.example.spring.informationEntity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    default List<BeanDefinition> loadBeanDefinitions(Class<?> clazz){return null;};//对配置类进行扫描
    default List<BeanDefinition> loadBeanDefinitions(String location){return  null;};//对路径进行扫描

}

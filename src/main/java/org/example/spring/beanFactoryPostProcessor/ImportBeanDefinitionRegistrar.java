package org.example.spring.beanFactoryPostProcessor;

import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.informationEntity.AnnotationMetadata;

public interface ImportBeanDefinitionRegistrar {

    default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }
}

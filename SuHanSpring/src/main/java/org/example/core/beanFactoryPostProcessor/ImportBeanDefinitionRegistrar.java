package org.example.core.beanFactoryPostProcessor;

import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.informationEntity.AnnotationMetadata;

public interface ImportBeanDefinitionRegistrar {

    default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }
}

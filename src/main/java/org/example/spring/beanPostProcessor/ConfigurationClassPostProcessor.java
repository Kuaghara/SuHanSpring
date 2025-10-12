package org.example.spring.beanPostProcessor;

import org.example.spring.beanFactoryPostProcessor.PriorityOrdered;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;

public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}

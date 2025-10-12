package org.example.spring.beanFactoryPostProcessor;

import org.example.spring.beanPostProcessor.BeanDefinitionRegistryPostProcessor;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;

import java.util.List;

public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor,PriorityOrdered {
    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        List<String> beanNames = registry.getBeanNameForType(Configuration.class);
        for(String beanName : beanNames){

        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }
}

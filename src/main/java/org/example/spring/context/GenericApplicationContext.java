package org.example.spring.context;

import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.DefaultListableBeanFactory;
import org.example.spring.informationEntity.BeanDefinition;

import java.util.List;
import java.util.Map;

public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final DefaultListableBeanFactory beanFactory;
    ClassLoader classLoader;

    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public DefaultListableBeanFactory getBeanFactory() throws IllegalStateException {
        return beanFactory;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void registerAllBeanDefinition(List<BeanDefinition> beanDefinitionList) {
        beanFactory.registerAllBeanDefinition(beanDefinitionList);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanFactory.getBeanDefinition(beanName);
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanFactory.getBeanDefinitionMap();
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        beanFactory.removeBeanDefinition(beanName);
    }

    @Override
    public List<String> getBeanDefinitionNames() {
        return beanFactory.getBeanDefinitionNames();
    }
}

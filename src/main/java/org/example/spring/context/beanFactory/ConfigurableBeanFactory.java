package org.example.spring.context.beanFactory;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    void setParentBeanFactory(BeanFactory parentBeanFactory);

    BeanFactory getParentBeanFactory();

    void setBeanClassLoader(ClassLoader beanClassLoader);
}

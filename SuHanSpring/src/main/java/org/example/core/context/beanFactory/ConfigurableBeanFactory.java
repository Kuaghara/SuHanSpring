package org.example.core.context.beanFactory;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    BeanFactory getParentBeanFactory();

    void setParentBeanFactory(BeanFactory parentBeanFactory);

    void setBeanClassLoader(ClassLoader beanClassLoader);

    void preInstantiateSingletons();
}

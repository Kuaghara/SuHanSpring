package org.example.spring.context.BeanFactory;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory,SingletonBeanRegistry{
    void setParentBeanFactory(BeanFactory parentBeanFactory);
    BeanFactory getParentBeanFactory();
    void setBeanClassLoader(ClassLoader beanClassLoader);
}

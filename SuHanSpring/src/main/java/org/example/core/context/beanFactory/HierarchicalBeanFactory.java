package org.example.core.context.beanFactory;

//参考spring，此接口用于获取bean的父类工厂
public interface HierarchicalBeanFactory extends BeanFactory {
    @Override
    Object getBean(String beanName);

    @Override
    <T> T getBean(Class<T> clazz);

    @Override
    public <T> T getBean(String benaName, Class<T> clazz) throws Exception;

    @Override
    public Boolean containsBean(String beanName);

    public BeanFactory getParentBeanFactory();

    public void setParentBeanFactory(BeanFactory beanFactory);

}

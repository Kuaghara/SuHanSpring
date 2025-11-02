package org.example.spring.context.beanFactory;

//参考spring，此接口用于获取bean的父类工厂
public interface HierarchicalBeanFactory extends BeanFactory {
    @Override
    public Object getBean(String beanName);

    @Override
    public <T> T getBean(String benaName, Class<T> clazz) throws Exception;

    @Override
    public Boolean containsBean(String beanName);

    public void setParentBeanFactory(BeanFactory beanFactory);

    public BeanFactory getParentBeanFactory();

}

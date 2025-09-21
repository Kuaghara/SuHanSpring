package org.example.spring.context.beanFactory;

public interface BeanFactory {

    public Object getBean(String beanName) throws Exception;

    public <T> T getBean(String benaName, Class<T> clazz) throws Exception;

    Boolean containsBean(String beanName);
}

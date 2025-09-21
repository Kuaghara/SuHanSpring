package org.example.spring.context.beanFactory;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    Object getEarlyBean(String beanName);

    void registerEarlyBean(String beanName, Object bean);

    void addFactory(String name , ObjectFactory<Object> factory);

    void removeFactory(String beanName);

    Object getFactory(String beanName);

}

package org.example.core.context.beanFactory;

import java.util.List;

public interface BeanFactory {

    public Object getBean(String beanName) throws Exception;

    public <T> T getBean(String beanName, Class<T> clazz) throws Exception;

    public <T> T getBean(Class<T> clazz) throws Exception;

    Boolean containsBean(String beanName);

    Boolean isTypeMatch(String name, Class<?> clazz);

    List<String> getBeanNameForType(Class<?> clazz);

    void addBean(String name ,Object bean);
}

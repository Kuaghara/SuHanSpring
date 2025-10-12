package org.example.spring.context.beanFactory;

import java.util.List;

public interface BeanFactory {

    public Object getBean(String beanName) throws Exception;

    public <T> T getBean(String benaName, Class<T> clazz) throws Exception;

    Boolean containsBean(String beanName);

    Boolean isTypeMatch(String name , Class<?> clazz );

    List<String> getBeanNameForType(Class<?> clazz);
}

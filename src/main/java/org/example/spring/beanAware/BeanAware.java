package org.example.spring.beanAware;

public interface BeanAware {
    default void beanNameAware(String beanName){};
    default void beanClassAware(Class<?> beanClass){};
    default void beanScopeAware(String scope){};
    default void beanLazyAware(String  lazy){};
}

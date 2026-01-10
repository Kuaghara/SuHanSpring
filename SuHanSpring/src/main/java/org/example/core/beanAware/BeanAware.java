package org.example.core.beanAware;

public interface BeanAware {
    default void beanNameAware(String beanName) {
    }

    ;

    default void beanClassAware(Class<?> beanClass) {
    }

    ;

    default void beanScopeAware(String scope) {
    }

    ;

    default void beanLazyAware(Boolean lazy) {
    }

    ;
}

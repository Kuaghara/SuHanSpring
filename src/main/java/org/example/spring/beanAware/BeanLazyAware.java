package org.example.spring.beanAware;

public interface BeanLazyAware extends BeanAware {
    @Override
    default void beanLazyAware(Boolean lazy) {
        BeanAware.super.beanLazyAware(lazy);
    }
}

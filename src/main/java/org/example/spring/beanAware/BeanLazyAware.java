package org.example.spring.beanAware;

public interface BeanLazyAware extends BeanAware{
    @Override
    default void beanLazyAware(String lazy) {
        BeanAware.super.beanLazyAware(lazy);
    }
}

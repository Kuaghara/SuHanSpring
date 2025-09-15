package org.example.spring.beanAware;

public interface BeanNameAware extends BeanAware{
    @Override
    default void beanNameAware(String beanName) {
        BeanAware.super.beanNameAware(beanName);
    }
}

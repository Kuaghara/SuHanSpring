package org.example.spring.beanAware;

public interface BeanClassAware extends BeanAware{
    @Override
    default void beanClassAware(Class<?> beanClass) {
        BeanAware.super.beanClassAware(beanClass);
    }
}

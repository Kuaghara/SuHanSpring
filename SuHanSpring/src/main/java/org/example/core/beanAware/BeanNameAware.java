package org.example.core.beanAware;

public interface BeanNameAware extends BeanAware {
    @Override
    default void beanNameAware(String beanName) {
        BeanAware.super.beanNameAware(beanName);
    }
}

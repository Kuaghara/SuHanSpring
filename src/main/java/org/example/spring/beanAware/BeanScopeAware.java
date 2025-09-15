package org.example.spring.beanAware;

public interface BeanScopeAware extends BeanAware{
    @Override
    default void beanScopeAware(String scope) {
        BeanAware.super.beanScopeAware(scope);
    }
}

package org.example.core.context.beanFactory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws Exception;
}

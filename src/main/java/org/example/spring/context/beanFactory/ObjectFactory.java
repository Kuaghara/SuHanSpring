package org.example.spring.context.beanFactory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws Exception;
}

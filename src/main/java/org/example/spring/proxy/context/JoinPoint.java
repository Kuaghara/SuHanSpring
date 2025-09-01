package org.example.spring.proxy.context;

public interface JoinPoint {


    Object proceed() throws Throwable;

    Object getThis();
}

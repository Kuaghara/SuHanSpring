package org.example.core.proxy.context;

public interface JoinPoint {

    Object proceed() throws Throwable;

    Object getThis();
}

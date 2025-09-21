package org.example.spring.proxy.context;

public interface Advice {
    Object invoke(MethodInvocation invocation) throws Throwable;
}

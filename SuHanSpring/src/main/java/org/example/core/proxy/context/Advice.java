package org.example.core.proxy.context;

public interface Advice {
    Object invoke(MethodInvocation invocation) throws Throwable;
}

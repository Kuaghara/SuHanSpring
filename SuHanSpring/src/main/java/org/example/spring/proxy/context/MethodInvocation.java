package org.example.spring.proxy.context;

import java.lang.reflect.Method;

public interface MethodInvocation {
    Object getTarget();

    /**
     * 获得被代理的方法
     *
     * @return 被代理的方法
     */
    Method getMethod();

    /**
     * 获得调用代理方法时所传的实参
     *
     * @return 调用代理方法时所传的实参
     */
    Object getArguments();

    Object invoke() throws Throwable;

    Object proceed();
}

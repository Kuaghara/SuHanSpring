package org.example.spring.proxy.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;



public class JdkProxyFactory implements ProxyFactory, InvocationHandler {
    private final Object target;


    public JdkProxyFactory(Object target) {
        this.target = target;
    }

    //此处为jdk代理实现的方法，莫要多想
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JdkMethodInvocation methodInvocation = new JdkMethodInvocation(target, method, args);
        return methodInvocation.invoke(methodInvocation);
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }


    @Override
    public void addAdvisor(Advisor advisor) {

    }
}

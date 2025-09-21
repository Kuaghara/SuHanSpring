package org.example.spring.proxy.context;

public class ProceedingJoinPoint implements JoinPoint {

    private MethodInvocation methodInvocation;

    public ProceedingJoinPoint(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    @Override
    public Object proceed() throws Throwable {
        return this.methodInvocation.proceed();
    }

    @Override
    public Object getThis() {
        return this;
    }
}

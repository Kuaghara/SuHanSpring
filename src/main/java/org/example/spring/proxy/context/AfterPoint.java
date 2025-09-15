package org.example.spring.proxy.context;

import org.example.spring.proxy.annotation.After;
import org.example.spring.proxy.annotation.Before;

import java.lang.reflect.Method;

public class AfterPoint implements PointParser{

    @Override
    public Advisor getAdvisor(Method method, Object aspect) {
        String path = method.getDeclaredAnnotation(After.class).path();
        String pathMethodName = path.substring(path.lastIndexOf(".") + 1, path.lastIndexOf("("));
        String pathClassName = path.substring(0, path.lastIndexOf("."));
        Advisor advisor = new Advisor(){

            @Override
            public boolean classFilter(Class<?> targetClass) {
                return getPointcut().classFilter(targetClass);
            }

            @Override
            public Advice getAdvice() {
                return invocation -> {
                   Object result = invocation.proceed();
                    method.invoke(aspect);
                    return result;
                };
            }

            @Override
            public Pointcut getPointcut() {
                return new Pointcut() {
                    public boolean classFilter(Class<?> targetClass) {
                        return pathClassName.equals(targetClass.getName());
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return pathMethodName.equals(method.getName());
                    }
                };
            }
        };
        return advisor;
    }
}
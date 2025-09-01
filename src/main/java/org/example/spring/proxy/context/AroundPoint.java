package org.example.spring.proxy.context;

import org.example.spring.proxy.annotation.Around;
import org.example.spring.proxy.annotation.Aspect;

import java.lang.reflect.Method;

public class AroundPoint implements PointParser{
    @Override
    public Advisor getAdvisor(Method method, Object aspect) {
        Method[] methods = aspect.getClass().getDeclaredMethods();
        Method AroundMethod = null;
        for (Method m : methods){
            if(m.isAnnotationPresent(Around.class)){
                AroundMethod = m;
            }
        }

        String path = method.getDeclaredAnnotation(Around.class).path();
        String pathClassName = path.substring(0, path.lastIndexOf("."));
        String pathMethodName = path.substring(path.lastIndexOf(".") + 1, path.lastIndexOf("("));

        Method finalAroundMethod = AroundMethod;
        return new Advisor() {

            @Override
            public Advice getAdvice() {
                return methodInvocation->{
                    ProceedingJoinPoint joinPoint = new ProceedingJoinPoint(methodInvocation);
                    return finalAroundMethod.invoke(aspect,joinPoint);
                };
            }

            @Override
            public Pointcut getPointcut() {
                return new Pointcut() {

                    @Override
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
    }
}

package org.example.spring.proxy.context;

public interface Advisor {
    default boolean classFilter(Class<?> targetClass) {
        return getPointcut().classFilter(targetClass);
    }

    Advice getAdvice();

    Pointcut getPointcut();

}

package org.example.spring.proxy.context;

import java.lang.reflect.Method;

public interface Pointcut {

    boolean classFilter(Class<?> targetClass);

    boolean matches(Method method, Class<?> targetClass);

}

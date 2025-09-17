package org.example.spring.proxy.context;

import org.example.spring.proxy.annotation.Around;
import org.example.spring.proxy.annotation.Async;

import java.lang.reflect.Method;

public class AsyncPoint implements PointParser {
    @Override
    public Advisor getAdvisor(Method method, Object Aspect) {
        Method[] methods = Aspect.getClass().getDeclaredMethods();
        Method asyncMethod = null;
        for (Method m : methods){
            if(m.isAnnotationPresent(Async.class)){
                asyncMethod = m;
            }
        }

        String path = method.getDeclaredAnnotation(Around.class).path();
        String pathClassName = path.substring(0, path.lastIndexOf("."));
        String pathMethodName = path.substring(path.lastIndexOf(".") + 1, path.lastIndexOf("("));

        return new Advisor() {

            @Override
            public Advice getAdvice() {
                return methodInvocation->{
                   return new Thread(() -> {
                        try {
                            asyncMethod.invoke(Aspect);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
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

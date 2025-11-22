package org.example.spring.proxy.context;

import org.example.spring.context.ThreadPoolManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

public class AsyncPoint implements PointParser {

    private static ExecutorService executorService = ThreadPoolManager.getThreadPool();
    @Override
    public Advisor getAdvisor(Method amethod, Object aspect) {
        Class<?> clazz = aspect.getClass();

        return new Advisor() {

            @Override
            public Advice getAdvice() {
                Callable<Object> task = () -> {
                    Object invoke;
                    try {
                         invoke = amethod.invoke(aspect);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    if (invoke instanceof Future<?>){
                        return ((Future<?>) invoke).get();
                    }
                    return null;
                };
                //这里就不换lambda表达式了，以免又看不懂了
                return new Advice() {
                    @Override
                    public Object invoke(MethodInvocation invocation) throws Throwable {
                        executorService.submit(task);
                        return null;
                    }
                };
            }

            @Override
            public Pointcut getPointcut() {
                return new Pointcut() {

                    @Override
                    public boolean classFilter(Class<?> targetClass) {
                        return clazz.equals(targetClass);
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return amethod.equals(method);
                    }
                };
            }
        };
    }
}

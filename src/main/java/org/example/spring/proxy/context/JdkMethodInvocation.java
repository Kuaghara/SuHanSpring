package org.example.spring.proxy.context;

import java.lang.reflect.Method;

import static org.example.spring.proxy.context.AnnotationResolver.advisorList;

public class JdkMethodInvocation implements MethodInvocation {
    private Object target;//此为原对象
    private Method method;//此为原方法
    private Object[] arguments;//此为原方法的参数
    private int index = 0;


    public JdkMethodInvocation(Object target, Method method, Object[] arguments) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object getArguments() {
        return arguments;
    }


    private Integer getIndex() {
        return index++;
    }


    @Override
    public Object invoke() throws Throwable {
        return method.invoke(target, arguments);
    }

    public Object invoke(JdkMethodInvocation invocation) throws Throwable {
        int index = getIndex();
        if (index == advisorList.size()) {
            return invocation.invoke();
        }
        Advisor advisor = advisorList.get(index);
        Pointcut pointcut = advisor.getPointcut();
        //判断切点是否匹配，是就执行增强方法，不是就继续
        return pointcut.matches(invocation.getMethod(), target.getClass()) ?
                advisor.getAdvice().invoke(invocation) : invoke(invocation);
    }

    @Override
    public Object proceed() {
        try {
            return invoke(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


}

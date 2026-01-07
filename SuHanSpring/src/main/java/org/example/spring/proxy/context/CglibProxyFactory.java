package org.example.spring.proxy.context;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyFactory implements ProxyFactory, MethodInterceptor {
    private Object target;

    public CglibProxyFactory(Object target) {
        this.target = target;
    }


    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public void addAdvisor(Advisor advisor) {
    }

    @Override
    //第一个参数：动态生成的代理实例
    //第二个参数：被代理的方法的引用
    //第三个参数：被代理的方法参数
    //第四个参数：生成的代理类的代理方法引用
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        CglibMethodInvocation methodInvocation = new CglibMethodInvocation(target, method, objects);
        return methodInvocation.invoke(methodInvocation);
    }
    /*
    此处粘贴一份方法的正常使用
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("Cglib代理测试，此为方法调用前输出");
        Object invoked = method.invoke(target);
        System.out.println("Cglib代理测试，此为方法调用后输出");
        return invoked;
    }
     */

}

package org.example.entity;

import org.example.spring.Annotation.Component;
import org.example.spring.proxy.annotation.Around;
import org.example.spring.proxy.annotation.Aspect;
import org.example.spring.proxy.annotation.Before;
import org.example.spring.proxy.context.JoinPoint;
import org.example.spring.proxy.context.MethodInvocation;
import org.example.spring.proxy.context.ProceedingJoinPoint;

import java.lang.reflect.Method;

@Component
@Aspect
public class TestProxy {
    @Around(path = "org.example.entity.UserService.test()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before_around");
        joinPoint.proceed();
        System.out.println("after");
    }
    @Before(path = "org.example.entity.UserService.test()")
    public void before() throws Throwable {
        System.out.println("before_before");
    }
}

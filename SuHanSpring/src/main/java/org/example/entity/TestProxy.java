package org.example.entity;

import org.example.core.annotation.Component;
import org.example.core.proxy.annotation.Around;
import org.example.core.proxy.annotation.Aspect;
import org.example.core.proxy.annotation.Before;
import org.example.core.proxy.context.ProceedingJoinPoint;

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

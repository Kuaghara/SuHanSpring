package org.example.entity;


import org.example.spring.Annotation.Autowired;
import org.example.spring.Annotation.Component;
import org.example.spring.Annotation.Scope;
import org.example.spring.beanPostProcessor.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


@Component
public class UserService  {
    String name;

    @Autowired
    public OldUserService old;

    public OldUserService getOld() {
        return old;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void test(){
        System.out.println("hello world");
    }



}
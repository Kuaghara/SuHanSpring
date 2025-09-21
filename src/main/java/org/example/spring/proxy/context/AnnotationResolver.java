package org.example.spring.proxy.context;

import org.example.spring.proxy.annotation.After;
import org.example.spring.proxy.annotation.Around;
import org.example.spring.proxy.annotation.Before;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationResolver {
    public static List<Advisor> advisorList = new ArrayList<>();

    public static boolean classFilter(Class<?> beanClass) {
        for (Advisor advisor : advisorList) {
            return advisor.classFilter(beanClass);
        }
        return false;
    }

    public static void register(Advisor advisor) {
        advisorList.add(advisor);
    }

    //将声明类的切面方法解析成Advisor
    //1.获取bean内的所有方法
    //2.解析方法上的注解
    //3.将解析结果封装成Advisor
    //4.将Advisor打包返回
    public void parse(Class<?> beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Before.class)) {
                //走进此处的应该为AOP方法
                try {
                    BeforePoint beforePoint = new BeforePoint();
                    Object aspect = beanClass.getDeclaredConstructor().newInstance();
                    Advisor advisor = beforePoint.getAdvisor(method, aspect);
                    advisorList.add(advisor);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            if (method.isAnnotationPresent(After.class)) {
                try {
                    Object aspect = beanClass.getDeclaredConstructor().newInstance();
                    AfterPoint afterPoint = new AfterPoint();
                    Advisor advisor = afterPoint.getAdvisor(method, aspect);
                    advisorList.add(advisor);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            if (method.isAnnotationPresent(Around.class)) {
                try {

                    Object aspect = beanClass.getDeclaredConstructor().newInstance();
                    AroundPoint aroundPoint = new AroundPoint();
                    Advisor advisor = aroundPoint.getAdvisor(method, aspect);
                    advisorList.add(advisor);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


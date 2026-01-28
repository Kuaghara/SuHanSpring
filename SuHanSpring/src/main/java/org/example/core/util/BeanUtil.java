package org.example.core.util;

import org.example.core.beanAware.BeanNameAware;
import org.example.core.context.beanFactory.BeanFactory;
import org.example.core.context.beanFactory.DefaultListableBeanFactory;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class BeanUtil {

    private static DefaultListableBeanFactory beanFactory;


    public static void setBeanFactory(DefaultListableBeanFactory beanFactory) {
        BeanUtil.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiateClass(String className) {
        try {
            // 1. 加载类 (对应 Spring 的 ClassUtils.forName)
            Class<?> clazz = Class.forName(className);
            // 2. 实例化
            return (T) instantiateClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("无法找到类: " + className, e);
        }
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new RuntimeException("无法实例化接口: " + clazz.getName());
        }

        try {
            // 1. 获取无参构造函数 (Spring 默认寻找无参构造)
            Constructor<T> constructor = clazz.getDeclaredConstructor();

            // 2. 暴力反射 (允许访问 private 构造函数)
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            // 3. 执行构造函数
            return constructor.newInstance();

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("类 " + clazz.getName() + " 没有无参构造函数", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("实例化类 " + clazz.getName() + " 失败: " + e.getMessage(), e);
        }
    }

    public static void applyAware(Object o){
        beanFactory.applyAware( o);
    }
}
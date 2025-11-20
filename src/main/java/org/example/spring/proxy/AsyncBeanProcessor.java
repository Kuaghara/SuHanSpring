package org.example.spring.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.example.spring.beanPostProcessor.MergedBeanDefinitionPostProcessor;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.proxy.annotation.Async;
import org.example.spring.proxy.context.AnnotationResolver;
import org.example.spring.proxy.context.CglibProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.example.spring.proxy.context.AnnotationResolver.advisorList;

public class AsyncBeanProcessor implements MergedBeanDefinitionPostProcessor {
    Map<Class<?>,List<Method>> asyncMethods = new HashMap<>();
    Object target = null;
    @Override
    public void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanClass, String beanName) {
        for(Method method : beanClass.getDeclaredMethods()){
            if (method.isAnnotationPresent(Async.class)){
                AnnotationResolver annotationResolver = new AnnotationResolver();
                annotationResolver.parseAsync(beanClass,method);
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(AnnotationResolver.classFilter(bean.getClass())){
            return new CglibProxyFactory(bean).getProxy();
        }
        return bean;
    }

}

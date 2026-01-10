package org.example.core.proxy;

import org.example.core.beanPostProcessor.MergedBeanDefinitionPostProcessor;
import org.example.core.informationEntity.BeanDefinition;
import org.example.core.proxy.annotation.Async;
import org.example.core.proxy.context.AnnotationResolver;
import org.example.core.proxy.context.CglibProxyFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

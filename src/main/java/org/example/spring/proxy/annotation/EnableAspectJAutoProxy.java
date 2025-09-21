package org.example.spring.proxy.annotation;


import org.example.spring.annotation.Import;
import org.example.spring.beanPostProcessor.AsyncBeanProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(AsyncBeanProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableAspectJAutoProxy {
}

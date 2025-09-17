package org.example.spring.proxy.annotation;


import org.example.spring.Annotation.Import;
import org.example.spring.beanPostProcessor.AsyncBeanProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(AsyncBeanProcessor.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableAsync {
}

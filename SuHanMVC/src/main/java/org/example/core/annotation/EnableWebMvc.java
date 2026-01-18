package org.example.core.annotation;

import org.example.core.beanPostProcessor.HttpServerBeanPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HttpServerBeanPostProcessor.class)
public @interface EnableWebMvc {
}

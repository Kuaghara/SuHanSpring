package org.example.core.proxy.annotation;


import org.example.core.annotation.Import;
import org.example.core.proxy.AsyncBeanProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(AsyncBeanProcessor.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableAsync {
}

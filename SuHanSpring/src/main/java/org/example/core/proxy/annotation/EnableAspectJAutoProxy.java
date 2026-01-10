package org.example.core.proxy.annotation;


import org.example.core.annotation.Import;
import org.example.core.proxy.ProxyBeanPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(ProxyBeanPostProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableAspectJAutoProxy {
}

package org.example.core.annotation;

/// 有设计，未实现
public @interface CookieValue {
    String value() ;
    boolean required() default true;
}

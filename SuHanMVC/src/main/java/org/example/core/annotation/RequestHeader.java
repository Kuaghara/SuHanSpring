package org.example.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {
    String value();

    String defaultValue() default "";

    boolean required() default true;
}

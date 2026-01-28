package org.example.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {
    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}

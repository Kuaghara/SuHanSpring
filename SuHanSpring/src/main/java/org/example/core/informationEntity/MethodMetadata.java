package org.example.core.informationEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface MethodMetadata {

    Annotation getAnnotation(String annotationName);
    Class<?> getReturnType();
    Method getMethod();
    boolean containsMethodAnnotation(Class<? extends Annotation> annotationType);
}

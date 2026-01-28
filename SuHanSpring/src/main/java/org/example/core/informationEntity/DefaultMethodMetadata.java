package org.example.core.informationEntity;

import org.example.core.util.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultMethodMetadata implements MethodMetadata{

    Method method;
    List<Annotation> annotations;

    public DefaultMethodMetadata(Method method) {
        this.method = method;
        this.annotations = AnnotationUtil.getAnnotationsList(method);
    }

    @Override
    public Annotation getAnnotation(String annotationName) {
        for(Annotation annotation:annotations){
            if(annotation.annotationType().getSimpleName().equals(annotationName)){
                return annotation;
            }
        }
        return null;
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public boolean containsMethodAnnotation(Class<? extends Annotation> annotationType) {
        return AnnotationUtil.listIncludeAnnotation(annotations, annotationType);
    }
}
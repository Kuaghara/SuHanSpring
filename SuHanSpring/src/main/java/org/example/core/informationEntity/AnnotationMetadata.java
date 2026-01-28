package org.example.core.informationEntity;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/// 26/1/24重新启用，因为在编写OmMeissingBeanCondition的时候无法通过注解获取他做
public interface AnnotationMetadata extends ClassMetadata {

    Set<String> getAnnotationTypes();

    boolean hasAnnotation(String annotationName);

    Annotation getAnnotation(String annotationName);
}
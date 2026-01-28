package org.example.core.informationEntity;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface AnnotatedTypeMetadata {

    AnnotationMetadata getAnnotationMetadata();

    MethodMetadata getMethodMetadata();

    Annotation getAnnotationFromType(String annotationName);

    Annotation getAnnotationFromMethod(String annotationName);

}

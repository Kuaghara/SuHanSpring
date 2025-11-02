package org.example.spring.informationEntity;
import java.lang.annotation.Annotation;
import java.util.Set;

public interface AnnotationMetadata <T> extends ClassMetadata {

    Set<String> getAnnotationTypes();
    boolean hasAnnotation(String annotationName);

    Annotation getAnnotation();
    T getValue();
}
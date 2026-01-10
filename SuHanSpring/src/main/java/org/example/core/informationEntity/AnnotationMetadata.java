package org.example.core.informationEntity;
import java.lang.annotation.Annotation;
import java.util.Set;

public interface AnnotationMetadata <T> extends ClassMetadata {

    Set<String> getAnnotationTypes();
    boolean hasAnnotation(String annotationName);

    Annotation getAnnotation();
    T getValue();
}
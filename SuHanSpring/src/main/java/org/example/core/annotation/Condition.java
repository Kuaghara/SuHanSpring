package org.example.core.annotation;


import org.example.core.context.ApplicationContext;
import org.example.core.informationEntity.AnnotatedTypeMetadata;import org.example.core.informationEntity.AnnotationMetadata;import java.lang.reflect.AnnotatedArrayType;

public interface Condition {
    //容器以及注解的类
    boolean matches(ApplicationContext context, AnnotatedTypeMetadata annotatedTypeMetadata);
}

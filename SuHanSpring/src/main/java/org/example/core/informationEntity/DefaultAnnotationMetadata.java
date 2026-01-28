package org.example.core.informationEntity;

import org.example.core.util.AnnotationUtil;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultAnnotationMetadata implements AnnotationMetadata{

    private final Class<?> clazz; // 被注解的类

    private List<Annotation> annotations; // 注解列表


    public DefaultAnnotationMetadata(Class<?> clazz) {
        this.clazz = clazz;
        annotations = AnnotationUtil.getAnnotationsList(clazz, new ArrayList<>());
    }


    @Override
    public Set<String> getAnnotationTypes() {
        return annotations.stream()
                .map(Annotation::annotationType)
                .map(Class::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasAnnotation(String annotationName) {
        return annotations.stream()
                .map(Annotation::annotationType)
                .map(Class::getSimpleName)
                .anyMatch(annotationName::equals);
    }

    @Override
    public Annotation getAnnotation(String annotationName) {
        return annotations.stream()
                .filter(annotation -> annotation.annotationType().getSimpleName().equals(annotationName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getClassName() {
        return clazz.getSimpleName();
    }

    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return clazz.isAnnotation();
    }

    @Override
    public boolean isAbstract() {
        return java.lang.reflect.Modifier.isAbstract(clazz.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return java.lang.reflect.Modifier.isFinal(clazz.getModifiers());
    }

    @Override
    public boolean isIndependent() {
        // 检查是否是顶级类或静态嵌套类（独立类）
        return clazz.getEnclosingClass() == null || java.lang.reflect.Modifier.isStatic(clazz.getModifiers());
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }
}
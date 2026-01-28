package org.example.core.annotation;

import org.example.core.context.ApplicationContext;
import org.example.core.informationEntity.AnnotatedTypeMetadata;
import org.example.core.informationEntity.AnnotationMetadata;
import org.example.core.informationEntity.MethodMetadata;
import org.example.core.util.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnClassCondition implements Condition {


    private boolean doMatch(ConditionalOnClass annotationOnClass, ClassLoader classLoader) {
        String[] value = annotationOnClass.name();
        for (String className : value) {
            if (!isClassInClasspath(className, classLoader)) {
                //存在没有的类
                return false;
            }
        }
        //所有类都存在
        return true;
    }

    private boolean isClassInClasspath(String className, ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean matches(ApplicationContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        AnnotationMetadata annotationMetadata = annotatedTypeMetadata.getAnnotationMetadata();
        Annotation ann = annotationMetadata.getAnnotation("ConditionalOnClass");

        //处理在类上的情况
        if (ann != null) {
            ConditionalOnClass annotationOnClass = (ConditionalOnClass) ann;
            return doMatch(annotationOnClass, Thread.currentThread().getContextClassLoader());
        }

        //处理在方法上的情况
        MethodMetadata method = annotatedTypeMetadata.getMethodMetadata();
            if (method.containsMethodAnnotation(ConditionalOnClass.class)) {
                ConditionalOnClass annotationOnClass = (ConditionalOnClass) method.getAnnotation("ConditionalOnClass");
                return doMatch(annotationOnClass, Thread.currentThread().getContextClassLoader());
            }

        return true; // 如果没有找到任何条件注解，默认条件满足
    }
}
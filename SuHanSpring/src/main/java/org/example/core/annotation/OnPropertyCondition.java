package org.example.core.annotation;

import org.example.core.context.ApplicationContext;
import org.example.core.informationEntity.AnnotatedTypeMetadata;
import org.example.core.informationEntity.AnnotationMetadata;
import org.example.core.informationEntity.MethodMetadata;

import java.lang.annotation.Annotation;

public class OnPropertyCondition implements Condition {

    @Override
    public boolean matches(ApplicationContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        // 检查被注解的类上是否有@ConditionalOnProperty注解
        AnnotationMetadata annotationMetadata = annotatedTypeMetadata.getAnnotationMetadata();
        if(annotationMetadata != null){
            Annotation conditionalOnPropertyAnn =
                    annotatedTypeMetadata.getAnnotationFromType(ConditionalOnProperty.class.getName());

            if (conditionalOnPropertyAnn instanceof ConditionalOnProperty) {
                ConditionalOnProperty conditionalOnProperty = (ConditionalOnProperty) conditionalOnPropertyAnn;

                String propertyName = conditionalOnProperty.name();
                String havingValue = conditionalOnProperty.havingValue();
                boolean matchIfMissing = conditionalOnProperty.matchIfMissing();

                // 从ApplicationContext获取配置属性值
                String propertyValue = getProperty(context, propertyName);

                // 如果属性不存在
                if (propertyValue == null) {
                    return matchIfMissing;
                }

                // 如果指定了期望值
                if (!havingValue.isEmpty()) {
                    return havingValue.equals(propertyValue);
                }

                // 默认情况下，只要属性存在且不为空就满足条件
                return !propertyValue.isEmpty();
            }

        }

        // 检查方法上是否有@ConditionalOnProperty注解
        MethodMetadata methodMetadata = annotatedTypeMetadata.getMethodMetadata();
        if (methodMetadata != null) {
            if (methodMetadata.containsMethodAnnotation(ConditionalOnProperty.class)) {
                java.lang.annotation.Annotation methodAnn = 
                    methodMetadata.getAnnotation(ConditionalOnProperty.class.getSimpleName());
                if (methodAnn instanceof ConditionalOnProperty) {
                    ConditionalOnProperty conditionalOnProperty = (ConditionalOnProperty) methodAnn;
                    
                    String propertyName = conditionalOnProperty.name();
                    String havingValue = conditionalOnProperty.havingValue();
                    boolean matchIfMissing = conditionalOnProperty.matchIfMissing();

                    // 从ApplicationContext获取配置属性值
                    String propertyValue = getProperty(context, propertyName);

                    // 如果属性不存在
                    if (propertyValue == null) {
                        return matchIfMissing;
                    }

                    // 如果指定了期望值
                    if (!havingValue.isEmpty()) {
                        return havingValue.equals(propertyValue);
                    }

                    // 默认情况下，只要属性存在且不为空就满足条件
                    return !propertyValue.isEmpty();
                }
            }
        }

        // 如果没有@ConditionalOnProperty注解，则条件默认满足
        return true;
    }

    private String getProperty(ApplicationContext context, String key) {
        try {
            return context.getEnvironment().get(key);
        } catch (Exception e) {
            return null;
        }
    }
}
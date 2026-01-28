package org.example.core.annotation;

import org.example.core.context.ApplicationContext;
import org.example.core.informationEntity.AnnotatedTypeMetadata;
import org.example.core.informationEntity.AnnotationMetadata;
import org.example.core.informationEntity.MethodMetadata;
import org.example.core.util.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class OnMissingBeanCondition implements Condition {

    @Override
    public boolean matches(ApplicationContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {

        AnnotationMetadata annotationMetadata = annotatedTypeMetadata.getAnnotationMetadata();
        if(annotationMetadata != null ){
            Annotation com = annotationMetadata.getAnnotation("ConditionalOnMissingBean");
            // 处理类上的ConditionalOnMissingBean注解
            if (com != null) {
                Class<?>[] value = ((ConditionalOnMissingBean) com).value();
                for (Class<?> clazz1 : value) {
                    // 检查容器中是否已存在该类型的Bean实例，而不仅仅是Bean定义
                    try {
                        context.getBean(clazz1);
                        // 如果能找到该类型的Bean实例，则条件不满足
                        return false;
                    } catch (Exception e) {
                        // 如果找不到该类型的Bean实例，则条件满足
                        continue;
                    }
                }
            }
        }

        // 处理方法上的ConditionalOnMissingBean注解
        MethodMetadata methodMetadata = annotatedTypeMetadata.getMethodMetadata();
        if(methodMetadata !=  null) {
            if (methodMetadata.containsMethodAnnotation(ConditionalOnMissingBean.class)) {
                ConditionalOnMissingBean conditionalOnMissingBean =
                        (ConditionalOnMissingBean) methodMetadata.getAnnotation("ConditionalOnMissingBean");

                Class<?>[] value = conditionalOnMissingBean.value();
                if (value.length > 0) {
                    // 如果指定了具体的类型
                    for (Class<?> clazz1 : value) {
                        // 检查容器中是否已存在该类型的Bean实例
                        try {
                            context.getBean(clazz1);
                            // 如果存在该类型的Bean，则条件不满足，不应该创建新Bean
                            return false;
                        } catch (Exception e) {
                            // 如果找不到该类型的Bean实例，则条件满足
                            continue;
                        }
                    }
                } else {
                    // 如果没有指定具体类型，则检查方法的返回类型
                    Class<?> returnType = methodMetadata.getReturnType();
                    try {
                        context.getBean(returnType);
                        // 如果存在该返回类型的Bean，则条件不满足
                        return false;
                    } catch (Exception e) {
                        // 如果找不到该返回类型的Bean实例，则条件满足
                        return true;
                    }
                }
            }
        }
        // 如果没有任何ConditionalOnMissingBean注解，或者条件都满足（没有找到对应Bean），则返回true
        return true;
    }
}
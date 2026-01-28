package org.example.core.informationEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//当写到这里的时候，我已经不管运行效率什么的，优化与我没关系，我只想抓紧写完
//这里进行与spring不同写法，假如传入进来的是class，那我就只写针对class的东西
//传入的如果是method，只写针对该method的东西
//后面进行相应判断
/// 此处只针对各种condition判断进行编写使用
public class DefaultAnnotatedTypeMetadata implements AnnotatedTypeMetadata{

    private AnnotationMetadata annotationMetadata;

    //存储方法名称和方法元数据的映射
    private MethodMetadata methodMetadata;

    public DefaultAnnotatedTypeMetadata(Class<?> clazz){
        annotationMetadata = new DefaultAnnotationMetadata(clazz);
        methodMetadata = null;
    }

    public DefaultAnnotatedTypeMetadata(Method method){
        annotationMetadata = null;
        methodMetadata = new DefaultMethodMetadata(method);
    }

    @Override
    public MethodMetadata getMethodMetadata() {
        if(methodMetadata == null){
            return null;
        }
        return methodMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        if(annotationMetadata == null){
            return null;
        }
        return annotationMetadata;
    }

    @Override
    public Annotation getAnnotationFromType(String annotationName) {
        if( annotationMetadata == null){
            return null;
        }
        return annotationMetadata.getAnnotation(annotationName);
    }

    @Override
    public Annotation getAnnotationFromMethod( String annotationName) {
        if (methodMetadata == null) {
            return null;
        }
        return methodMetadata.getAnnotation(annotationName);
    }


}
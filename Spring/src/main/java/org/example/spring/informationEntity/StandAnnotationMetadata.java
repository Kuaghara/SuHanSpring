package org.example.spring.informationEntity;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//本来我是不打算用这个接口的，但是在编写ConfigurationClassParser的注解递归时
//我想要是单独保存有什么注解其实没啥用，要是遇到@Import，那我里面的value也是要的
//所以想着启用这个接口吧
public class StandAnnotationMetadata <T> implements AnnotationMetadata{
    Annotation thisAnnotation;
    List<Annotation> containAnnotations = new ArrayList<>();
    T value;

    @Override
    public Set<String> getAnnotationTypes() {
        return Set.of(containAnnotations.toString());
    }

    @Override
    public boolean hasAnnotation(String annotationName) {
        for(Annotation annotation : containAnnotations){
            if(annotation.annotationType().getName().equals(annotationName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Annotation getAnnotation() {
        return thisAnnotation;
    }

    @Override
    public T getValue() {
        return value;
    }


    @Override
    public String getClassName() {
        return "";
    }

    @Override
    public boolean isInterface() {
        return thisAnnotation.annotationType().isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return true;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isConcrete() {
        return AnnotationMetadata.super.isConcrete();
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isIndependent() {
        return false;
    }

}

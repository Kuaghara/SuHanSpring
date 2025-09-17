package org.example.spring.informationEntity;

import org.example.spring.Annotation.Lazy;
import org.example.spring.Annotation.Scope;
import org.example.spring.proxy.annotation.Aspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.AOP_LIST;

public class AnnotatedGenericBeanDefinition implements BeanDefinition{
    private Object className;
    private Class<?> clazz;
    private String scope;
    private String lazy;
    private List<Annotation> annotations = new ArrayList<>();
    private Map<AutoElement,Boolean> autoElementMap = new HashMap<>();//拿来存放字段以及是否被注入


    @Override
    public void setBeanDefinition(Class<?> clazz) {
        //查找Scope注解
        if(clazz.isAnnotationPresent(Scope.class)){
            Scope declaredAnnotation = clazz.getDeclaredAnnotation(Scope.class);
            setScope(declaredAnnotation.value());
        }
        else setScope("singleton");


        //查找Lazy注解
        if(clazz.isAnnotationPresent(Lazy.class)){
            Lazy declaredAnnotation = clazz.getDeclaredAnnotation(Lazy.class);
            setLazy(declaredAnnotation.value());
        }
        else setLazy("false");

        //查找@Aspect注解
        if(clazz.isAnnotationPresent(Aspect.class)){
            AOP_LIST.add(clazz);
        }

        setClassName(clazz.getSimpleName());
        setClazz(clazz);

        //特别实现
        addAllAnnotation(List.of(clazz.getAnnotations()));
    }

    @Override
    public void addAutoElement(AutoElement autoElement) {
        autoElementMap.put(autoElement,false);
    }

    @Override
    public void addAllAutoElement(List<AutoElement> autoElement) {
        for (AutoElement autoElement1 : autoElement) {
            autoElementMap.put(autoElement1,false);
        }
    }

    @Override
    public Map<AutoElement, Boolean> getAutoElementMap() {
        return autoElementMap ;
    }


    @Override
    public String getLazy() {
        return lazy;
    }

    @Override
    public void setLazy(String lazy) {
        this.lazy = lazy;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object getClassName() {
        return className;
    }

    @Override
    public void setClassName(Object className) {
        this.className = className;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    public void addOneAnnotation(Annotation annotation){
        annotations.add(annotation);
    }

    public void addAllAnnotation(List<Annotation> annotation){
        annotations.addAll(annotation);
    }

    public List<Annotation> getAllAnnotation(){
        return annotations;
    }
}

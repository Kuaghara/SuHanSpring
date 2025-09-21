package org.example.spring.informationEntity;

import org.example.spring.annotation.Lazy;
import org.example.spring.annotation.Scope;
import org.example.spring.proxy.annotation.Aspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnnotatedGenericBeanDefinition implements BeanDefinition {
    private String className;
    private Class<?> clazz;
    private String scope = "singleton";
    private Boolean lazy = false;
    private List<Annotation> annotations = new ArrayList<>();
    private Map<AutoElement, Boolean> autoElementMap = new HashMap<>();//拿来存放字段以及是否被注入
    private boolean aspect = false;


    @Override
    public void setBeanDefinition(Class<?> clazz) {
        //查找Scope注解
        if (clazz.isAnnotationPresent(Scope.class)) {
            Scope declaredAnnotation = clazz.getDeclaredAnnotation(Scope.class);
            setScope(declaredAnnotation.value());
        }

        //查找Lazy注解
        if (clazz.isAnnotationPresent(Lazy.class)) {
            Lazy declaredAnnotation = clazz.getDeclaredAnnotation(Lazy.class);
            setLazy(declaredAnnotation.value());
        }

        //查找@Aspect注解,Aspect注解中并没值，此处只需查找
        if (clazz.isAnnotationPresent(Aspect.class)) {
            setAspect( true );
        }

        setClassName(clazz.getSimpleName());
        setClazz(clazz);

        //特别实现
        addAllAnnotation(List.of(clazz.getAnnotations()));
    }

    @Override
    public void addAutoElement(AutoElement autoElement) {
        autoElementMap.put(autoElement, false);
    }

    @Override
    public void addAllAutoElement(List<AutoElement> autoElement) {
        for (AutoElement autoElement1 : autoElement) {
            autoElementMap.put(autoElement1, false);
        }
    }

    @Override
    public Map<AutoElement, Boolean> getAutoElementMap() {
        return autoElementMap;
    }


    @Override
    public boolean getLazy() {
        return lazy;
    }

    @Override
    public void setLazy(boolean lazy) {
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
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
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

    public void addOneAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    public void addAllAnnotation(List<Annotation> annotation) {
        annotations.addAll(annotation);
    }

    public List<Annotation> getAllAnnotation() {
        return annotations;
    }

    @Override
    public void setAspect(Boolean Aspect) {
        this.aspect = Aspect;
    }

    @Override
    public boolean getAspect() {
        return aspect;
    }
}

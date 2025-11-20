package org.example.spring.informationEntity;

import org.example.spring.annotation.Lazy;
import org.example.spring.annotation.Scope;
import org.example.spring.proxy.annotation.Aspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScannedGenericBeanDefinition implements BeanDefinition {
    private String className;
    private String scope = "singleton";
    private boolean lazy = false;
    private Class<?> clazz;
    private String path;
    private List<Annotation> annotations = new ArrayList<>();
    private Map<AutoElement, Boolean> autoElementMap = new HashMap<>();//拿来存放字段以及是否被注入
    private boolean isFullConfigurationClass = false;
    private boolean singleton = true;

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

        setClassName(clazz.getSimpleName());
        setClazz(clazz);

        //特别实现
        setPath(clazz.getPackage().getName() + "." + clazz.getSimpleName());
    }

    @Override
    public List<Annotation> getAllAnnotation() {
        return annotations;
    }

    @Override
    public void addOneAnnotation(Annotation annotation) {
        annotations.add( annotation);
    }

    @Override
    public void addAllAnnotation(List<Annotation> annotation) {
        annotations.addAll(annotation);
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public boolean isPrototype() {
        return !singleton;
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
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
        if(scope.equals("prototype")) {
            this.singleton = false;
        }
    }

    @Override
    public boolean isLazy() {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean isFullConfigurationClass() {
        return isFullConfigurationClass;
    }

    @Override
    public void setFullConfigurationClass(boolean fullConfigurationClass) {
        this.isFullConfigurationClass = fullConfigurationClass;
    }
}

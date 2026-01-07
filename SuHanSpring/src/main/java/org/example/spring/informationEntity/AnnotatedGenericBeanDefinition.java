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
    private Boolean isFullConfigurationClass = false;
    private boolean singleton = true;

    public AnnotatedGenericBeanDefinition() {}

    // 10.13:
    //在后续编写中中我感觉使用构造方法来自动构建一个BeanDefinition会相对舒服一些，之前的方法也不会乱动
    public AnnotatedGenericBeanDefinition(Class<?> clazz) {
        setBeanDefinition(clazz);
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

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
        if(scope.equals("prototype")) {
            this.singleton = false;
        }
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
    public boolean isFullConfigurationClass() {
        return isFullConfigurationClass;
    }

    @Override
    public void setFullConfigurationClass(boolean fullConfigurationClass) {
        isFullConfigurationClass = fullConfigurationClass;
    }
}

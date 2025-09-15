package org.example.spring.informationEntity;

import org.example.spring.Annotation.Lazy;
import org.example.spring.Annotation.Scope;
import org.example.spring.proxy.annotation.Aspect;

import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.AOP_LIST;

public class GenericBeanDefinition implements BeanDefinition{
    private Object className;//bean的名字
    private Class<?> clazz;//bean的Class反射类
    private String scope;//bean的作用域
    private String lazy;//bean的懒加载
    //其余的先不实现


    @Override
    public void setBeanDefinition(Class<?> clazz) {
        //查找Scope注解
        if(clazz.isAnnotationPresent(Scope.class)){
            Scope declaredAnnotation = clazz.getDeclaredAnnotation(Scope.class);
            setScope(declaredAnnotation.value());
        }

        //查找Lazy注解
        if(clazz.isAnnotationPresent(Lazy.class)){
            Lazy declaredAnnotation = clazz.getDeclaredAnnotation(Lazy.class);
            setLazy(declaredAnnotation.value());
        }

        //查找@Aspect注解
        if(clazz.isAnnotationPresent(Aspect.class)){
            AOP_LIST.add(clazz);
        }

        setClassName(clazz.getSimpleName());
        setClazz(clazz);
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
        return this.className;
    }

    @Override
    public void setClassName(Object className) {
        this.className = className;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getLazy() {
        return lazy;
    }

    @Override
    public void setLazy(String lazy) {
        this.lazy = lazy;
    }
}

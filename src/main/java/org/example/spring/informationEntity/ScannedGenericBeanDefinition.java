package org.example.spring.informationEntity;

import org.example.spring.Annotation.Lazy;
import org.example.spring.Annotation.Scope;
import org.example.spring.proxy.annotation.Aspect;

import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.AOP_LIST;

public class ScannedGenericBeanDefinition implements BeanDefinition{
    private String scope;
    private String lazy;
    private Class<?> clazz;
    private Object className;//在扫描完后名字存储的并不是名字
    private String path;


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
        setPath(clazz.getPackage().getName()+"."+clazz.getSimpleName());
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

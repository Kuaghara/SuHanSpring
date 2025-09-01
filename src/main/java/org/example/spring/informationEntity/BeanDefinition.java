package org.example.spring.informationEntity;

public class BeanDefinition {
    private Object className;//类名
    private Class<?> clazz;//获取实例对象
    private String scope;//作用域
    private String lazy;//懒加载

    public Class<?> getClazz() {
        return clazz;
    }

    public String getLazy() {
        return lazy;
    }

    public void setLazy(String lazy) {
        this.lazy = lazy;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object getClassName() {
        return className;
    }

    public Object setClassName(Object className) {
        this.className = className;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}

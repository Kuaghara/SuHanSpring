package org.example.spring;

public class BeanDefinition {
    private Object className;
    private Class<?> clazz;
    private String scope="singleton";
    private String lazy="false";

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

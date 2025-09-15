package org.example.spring.informationEntity;

public interface BeanDefinition {

    public String getLazy() ;
    public void setLazy(String lazy) ;
    public Class<?> getClazz();
    public void setClazz(Class<?> clazz);
    public Object getClassName();
    public void setClassName(Object className) ;
    public String getScope() ;
    public void setScope(String scope) ;
    public void setBeanDefinition(Class<?> clazz);
}

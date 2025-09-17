package org.example.spring.informationEntity;

import java.util.List;
import java.util.Map;

public interface BeanDefinition {

     String getLazy() ;
     void setLazy(String lazy) ;
     Class<?> getClazz();
     void setClazz(Class<?> clazz);
     Object getClassName();
     void setClassName(Object className) ;
     String getScope() ;
     void setScope(String scope) ;
     void setBeanDefinition(Class<?> clazz);
     void addAutoElement(AutoElement autoElement);
     void addAllAutoElement(List<AutoElement> autoElement);
     Map<AutoElement,Boolean> getAutoElementMap() ;

}

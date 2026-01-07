package org.example.spring.informationEntity;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface BeanDefinition {

    boolean isSingleton();
    boolean isPrototype();

    boolean isLazy();

    void setLazy(boolean lazy);

    Class<?> getClazz();

    void setClazz(Class<?> clazz);

    String getClassName();

    void setClassName(String className);

    String getScope();

    void setScope(String scope);

    void setBeanDefinition(Class<?> clazz);

    void addAutoElement(AutoElement autoElement);

    void addAllAutoElement(List<AutoElement> autoElement);

    //需要进行依赖注入的属性及其是否被注入
    Map<AutoElement, Boolean> getAutoElementMap();

    boolean isFullConfigurationClass();

    void setFullConfigurationClass(boolean fullConfigurationClass);
    List<Annotation> getAllAnnotation();
    void addOneAnnotation(Annotation annotation);
    void addAllAnnotation(List<Annotation> annotation);
}

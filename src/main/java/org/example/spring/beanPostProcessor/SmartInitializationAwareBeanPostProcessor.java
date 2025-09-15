package org.example.spring.beanPostProcessor;


import org.example.spring.informationEntity.BeanDefinition;

//这个是初始化的
public interface SmartInitializationAwareBeanPostProcessor extends BeanPostProcessor{
    //此接口设计给依赖注入使用
    default Object applyBeforeInitializationMethod(Object bean){return null;};
    default void applyAfterInitializationMethod(){};


}

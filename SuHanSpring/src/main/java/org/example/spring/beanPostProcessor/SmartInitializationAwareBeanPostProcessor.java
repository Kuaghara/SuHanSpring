package org.example.spring.beanPostProcessor;


import org.example.spring.context.beanFactory.ObjectFactory;

/// 此接口重启用于实现用于提前Aop使用
public interface SmartInitializationAwareBeanPostProcessor extends BeanPostProcessor {
    //此接口设计给依赖注入使用
    default Object applyBeforeInitializationMethod(Object bean) {
        return null;
    }


    default Object applyAfterInitializationMethod(String name , Object  bean) {return null;}



}

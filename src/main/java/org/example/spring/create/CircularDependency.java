package org.example.spring.create;


import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.*;

//处理可能存在的依赖循环
//思路：上层中已在singletonObjects未获取到对象，我忽视上锁的再次查找，再次查找若仍未找到则进入依赖循环判断逻辑
public class CircularDependency {
    private static Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();//三级缓存

    public Object doGetBean(String beanName) {
        Object bean = getEarlyBean(beanName);
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        for(Map.Entry<AutoElement , Boolean> entry : bd.getAutoElementMap().entrySet()){
            if(!entry.getValue()){
                Field field = entry.getKey().getField();
                String fieldName = field.getType().getSimpleName();
                ObjectFactory<?> oneObjectFactory = this.singletonFactories.get(fieldName);
                if (oneObjectFactory != null) {
                    try {
                        Object oneBean = oneObjectFactory.getObject();
                        field.setAccessible( true);
                        field.set(bean, oneBean);
                        removeFactory(beanName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return bean;
    }

    public static void addFactory(String beanName, ObjectFactory<Object> objectFactory) {
        singletonFactories.put(beanName, objectFactory);
    }

    public void removeFactory(String beanName) {
        singletonFactories.remove(beanName);
    }
}

package org.example.spring.create;


import java.util.HashMap;
import java.util.Map;

import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.getEarlyBean;
import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.putEarlyBean;

//处理可能存在的依赖循环
//思路：上层中已在singletonObjects未获取到对象，我忽视上锁的再次查找，再次查找若仍未找到则进入依赖循环判断逻辑
//我会尽可能的完成spring中的逻辑，但是本人阅读能力有限，无法完全复刻
public class CircularDependency{
    private static Map<String , ObjectFactory<?>> singletonFactories = new HashMap<>();//三级缓存
    public Object doGetBean(String beanName){
        Object bean = getEarlyBean(beanName);
        if(bean == null){
           ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
           if(objectFactory != null){
               try {
                   bean = objectFactory.getObject();
                   putEarlyBean(beanName, bean);
                   removeFactory(beanName);
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
           }
        }
        return bean;
    }

    public static void addFactory(String beanName, ObjectFactory<Object> objectFactory){
        singletonFactories.put(beanName, objectFactory);
    }
    public void removeFactory(String beanName){
        singletonFactories.remove(beanName);
    }
}

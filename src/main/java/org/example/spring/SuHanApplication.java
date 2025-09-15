package org.example.spring;

import org.example.spring.create.CircularDependency;
import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



//@Deprecated
//public class SuHanApplication {
//    Class<?> mainConfig;
//    public static Map<String, BeanDefinition> BEANDEFINITION_MAP = new HashMap<>();
//    public static Map<String, Object> singletonObjects = new HashMap<>();//spring里这样写的
//    public static ClassLoader SUHANCLASSLOADER = null;
//    public static Map<String, List<AutoElement>> INJECTIONELEMENT_MAP = new HashMap<>();
//
//
//
//    public SuHanApplication(Class<?> clazz) {
//        setClassLoader(clazz.getClassLoader());
//        this.mainConfig = clazz;
//        scan(clazz);
//        doCreat();
//    }
//
//
//    public Object getBean(String beanName) {
//        BeanDefinition beanDefinition = BEANDEFINITION_MAP.get(beanName);
//        if (beanDefinition == null) {
//            throw new RuntimeException(beanName + "不存在");
//        }
//        else {
//            if (beanDefinition.getScope().equals("singleton")) {
//                Object bean = singletonObjects.get(beanName);
//               if(bean != null){
//                   return bean;
//               }
//               else {
//                   CircularDependency circularDependency = new CircularDependency();
//                   return circularDependency.doGetBean(beanName);
//
//               }
//            }
//            else {
//                return creatBean(beanDefinition);
//            }
//        }
//    }
//
//    public static void setClassLoader(ClassLoader classLoader) {
//        SUHANCLASSLOADER = classLoader;
//    }
//
//}

package org.example.spring.create;

import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.AutowiredFieldElement;
import org.example.spring.informationEntity.AutowiredMethodElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.example.spring.SuHanApplication.*;
import static org.example.spring.create.CreatBeans.creatBean;

public class InjectingBeans {
    public static void injectingBean(String name) {

        if (INJECTIONELEMENT_MAP.containsKey( name)) {
            List<AutoElement> autoElements = INJECTIONELEMENT_MAP.get(name);

            for (AutoElement autoElement : autoElements) {

                injectField(autoElement);

            }
        }


    }

     private static void injectField(AutoElement autoElement) {
        Field field = autoElement.getField();
        field.setAccessible(true);
        String fieldName = field.getName().toLowerCase(Locale.ROOT);

        //从BEANDEFINITION_MAP中查找注入对象是否存在
        if(BEANDEFINITION_MAP.containsKey(fieldName)){
            BeanDefinition beanDefinition = BEANDEFINITION_MAP.get(fieldName);

            //判断被注入对象是不是单例
            if(SINGLETONBEAN_MAP.containsKey(fieldName)){
                try {
                    field.set(field.getDeclaringClass(), SINGLETONBEAN_MAP.get(fieldName));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                try {
                    field.set(field.getDeclaringClass(), creatBean(beanDefinition));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}



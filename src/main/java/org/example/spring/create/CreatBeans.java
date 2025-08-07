package org.example.spring.create;

import org.example.spring.BeanDefinition;

import java.lang.reflect.Constructor;
import java.util.Map;

import static org.example.spring.SuHanApplication.SINGLETONBEAN_MAP;
import static org.example.spring.SuHanApplication.SUHANCLASSLOADER;

public class CreatBeans {
    public static void creatSingletonBean(Class<?> clazz, Map<String , BeanDefinition> beandefinitionMap) {
        for (Map.Entry<String,BeanDefinition> entry : beandefinitionMap.entrySet()){
            BeanDefinition bd = entry.getValue();
            if(bd.getScope().equals("prototype")|bd.getLazy().equals("true")){
                return;
            }
            else{
                SINGLETONBEAN_MAP.put(entry.getKey(),creatBean(entry.getValue()));
            }
        }

    }
    public static Object creatBean(BeanDefinition bd){

        try {
            Constructor<?> constructor = SUHANCLASSLOADER.loadClass(bd.getClassName().toString()).getConstructor();
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}

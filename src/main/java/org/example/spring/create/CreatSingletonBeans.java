package org.example.spring.create;

import org.example.spring.BeanDefinition;

import java.util.List;
import java.util.Map;

import static org.example.spring.SuHanApplication.SINGLETONBEAN_MAP;

public class CreatSingletonBeans {
    public static void creatSingletonBean(Class<?> clazz, Map<String , BeanDefinition> beandefinitionMap) {
        for (Map.Entry<String,BeanDefinition> entry : beandefinitionMap.entrySet()){
            BeanDefinition bd = entry.getValue();
            if(bd.getScope().equals("prototype")|bd.getLazy().equals("true")){
                return;
            }
            else{
                SINGLETONBEAN_MAP.put(entry.getKey(),creatBean(beandefinition))
            }
        }

    }
    public static void creatBean(BeanDefinition bd){
        ClassLoader classLoader2 = bd.getClazz().getClassLoader();
        classLoader2.loadClass().getConstructor();


    }

}

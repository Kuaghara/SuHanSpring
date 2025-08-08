package org.example.spring;

import org.example.spring.Annotation.*;
import org.example.spring.informationEntity.BeanDefinition;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

import static org.example.spring.create.Creats.creat;
import static org.example.spring.scan.Scans.scan;


public class SuHanApplication {
    Class<?> mainConfig;
    public static Map<String, BeanDefinition> BEANDEFINITION_MAP = new HashMap<>();
    public static Map<String, Object> SINGLETONBEAN_MAP = new HashMap<>();
    public static ClassLoader SUHANCLASSLOADER =null;





    public SuHanApplication(Class<?> clazz) {
        setClassLoader(clazz.getClassLoader());
        this.mainConfig = clazz;
        scan(clazz);
        creat();

    }





    private Object createSingletonBean(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = beanDefinition.getClazz();
        String scope = beanDefinition.getScope();
        Constructor<?> constructors = clazz.getDeclaredConstructor();
        Object object = constructors.newInstance();
        return autowire(object);
    }
    private Object autowire(Object object1) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Class<?> clazz1 = object1.getClass();
        Field[] fields = clazz1.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                String beanName = null;

                for (Map.Entry<String, BeanDefinition> entry : BEANDEFINITION_MAP.entrySet()) {
                    if (entry.getValue().getClazz().equals(fieldType)) {
                        beanName = entry.getKey();
                        break;
                    }
                }

                if (beanName != null) {
                    field.setAccessible(true);
                    BeanDefinition beanDefinition = BEANDEFINITION_MAP.get(beanName);
                    if ("singleton".equals(beanDefinition.getScope())) {
                        field.set(object1, SINGLETONBEAN_MAP.get(beanName));
                    } else {
                        field.set(object1, createSingletonBean(beanDefinition));
                    }
                }
            }
        }
        return object1;
    }


    public Object getBean(String beanName) {
        BeanDefinition beanDefinition= BEANDEFINITION_MAP.get(beanName);
        if(beanDefinition==null){
            throw new RuntimeException(beanName+"不存在");
        }
        else{
            if(beanDefinition.getScope().equals("singleton")){
                return SINGLETONBEAN_MAP.get(beanName);
            }
            else{
                try {
                    return createSingletonBean(beanDefinition);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void setClassLoader(ClassLoader classLoader){
        SUHANCLASSLOADER=classLoader;
    }

}

package org.example.spring;

import org.example.spring.Annotation.*;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

import static org.example.spring.scan.Scans.scan;


public class SuHanApplication {

    Class<?> mainConfig;
    public static Map<String, BeanDefinition> beandefinitionMap = new HashMap<>();
    Map<String, Object> singletonbeanMap = new HashMap<>();


    public SuHanApplication(Class<? extends Object> clazz) {
        this.mainConfig = clazz;
        scan(clazz);

    }





    private Object createSingletonBean(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = beanDefinition.getClazz();
        String scope = beanDefinition.getScope();
        Constructor<?> constructors = clazz.getDeclaredConstructor();
        Object object = constructors.newInstance();
        return Autowire(object);
    }
    private Object Autowire(Object object1) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Class<?> clazz1 = object1.getClass();
        Field[] fields = clazz1.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                String beanName = null;

                for (Map.Entry<String, BeanDefinition> entry : beandefinitionMap.entrySet()) {
                    if (entry.getValue().getClazz().equals(fieldType)) {
                        beanName = entry.getKey();
                        break;
                    }
                }

                if (beanName != null) {
                    field.setAccessible(true);
                    BeanDefinition beanDefinition = beandefinitionMap.get(beanName);
                    if ("singleton".equals(beanDefinition.getScope())) {
                        field.set(object1, singletonbeanMap.get(beanName));
                    } else {
                        field.set(object1, createSingletonBean(beanDefinition));
                    }
                }
            }
        }
        return object1;
    }


    public Object getBean(String beanName) {
        BeanDefinition beanDefinition= beandefinitionMap.get(beanName);
        if(beanDefinition==null){
            throw new RuntimeException(beanName+"不存在");
        }
        else{
            if(beanDefinition.getScope().equals("singleton")){
                return singletonbeanMap.get(beanName);
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
}

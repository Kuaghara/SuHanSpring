package org.example.spring.create;

import org.example.spring.Annotation.Autowired;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
            //这步可以理解为实例化前吗？（没有去创造实例化前的那个接口的情况下）
            Constructor<?>[] constructors = SUHANCLASSLOADER.loadClass(bd.getClassName().toString()).getConstructors();
            Constructor<?> theConstructor = null;

            //先判断@Autowired注解数量及其报错情况
            int i = 0,j = 0;
            for (Constructor<?> constructor : constructors){
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    i++;
                    Autowired declaredAnnotation = constructor.getDeclaredAnnotation(Autowired.class);
                    if(declaredAnnotation.required()){
                        j++;
                    }
                }
            }
            if(j == i || ( j == 1 && i >= 2*j ) ){
                throw new RuntimeException("过多被Autowired注解的构造方法");
            }

            //获取构造方法,没有自定义就给无参，有就给自定义的
            if(constructors.length == 1){
                theConstructor = constructors[0];
            } else if (constructors.length == 2) {
                theConstructor = constructors[1];
            }
            else{
                //这里我是真不知道怎么办了，只能返回一个无参的了
                theConstructor = constructors[0];
            }

            return theConstructor.newInstance();
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }



    }

}

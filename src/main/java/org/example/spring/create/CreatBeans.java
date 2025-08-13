package org.example.spring.create;

import org.example.spring.Annotation.Autowired;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.example.spring.SuHanApplication.SINGLETONBEAN_MAP;
import static org.example.spring.SuHanApplication.SUHANCLASSLOADER;

public class CreatBeans {
    public static void creatSingletonBean(String beanName,BeanDefinition beanDefinition) {

            if(beanDefinition.getScope().equals("prototype")|beanDefinition.getLazy().equals("true")){
                return;
            }
            else{
                SINGLETONBEAN_MAP.put(beanName,creatBean(beanDefinition));
            }


    }
    public static Object creatBean(BeanDefinition bd){

        try {

            String className = bd.getClazz().getName();
            Constructor<?>[] constructors = SUHANCLASSLOADER.loadClass(className).getConstructors();
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
            if (i!=0) {
                if(j == i || ( j == 1 && i >= 2*j ) ){
                    throw new RuntimeException("过多被Autowired注解的构造方法");
                }
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

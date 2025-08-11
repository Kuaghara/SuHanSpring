package org.example.spring.scan;

import org.example.spring.Annotation.Autowired;
import org.example.spring.informationEntity.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.example.spring.SuHanApplication.BEANDEFINITION_MAP;
import static org.example.spring.SuHanApplication.INJECTIONELEMENT_MAP;

public class AutoWiredScans {

    //接下来对自动注入时注入点的扫描
    public static void autoWiredScan(Map<String, BeanDefinition> beandefinitionMap) {
        List<AutoElement> currElements = new ArrayList<>();

        for (Map.Entry<String, BeanDefinition> entry : beandefinitionMap.entrySet()) {

            //得到该对象，为将来的方法，字段扫描做准备
            Class<?> clazz = entry.getValue().getClazz();

            //先字段扫描
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    AutowiredFieldElement autowiredFieldElement = new AutowiredFieldElement();
                    autowiredFieldElement.setField(field);
                    if(field.getAnnotation(Autowired.class).required()){
                        autowiredFieldElement.setRequired(true);
                    }
                    else {
                        autowiredFieldElement.setRequired(false);
                    }
                    currElements.add(autowiredFieldElement);
                }
            }

            //再对方法进行扫描
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Autowired.class)) {
                    AutowiredMethodElement autowiredMethodElement = new AutowiredMethodElement();
                    autowiredMethodElement.setMethod(method);
                    if(method.getAnnotation(Autowired.class).required()){
                        autowiredMethodElement.setRequired(true);
                    }
                    else {
                        autowiredMethodElement.setRequired(false);
                    }
                    Field[] fields1 = clazz.getDeclaredFields();
                    String fieldName = method.getName().substring(3).toLowerCase(Locale.ROOT);

                    //我怕这里无法找到，所以写一个标记
                    int i = 0;
                    for (Field field : fields1) {
                        if(field.getName().equals(fieldName)){
                            autowiredMethodElement.setField(field);
                            i++;
                        }
                    }
                    if(i == 0){
                        System.out.println("查找方法自动注入时，未找到方法对应的变量名:"+fieldName);
                    }
                    currElements.add(autowiredMethodElement);
                }
            }

            //还有构造方法
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    AutowiredConstructorElement autowiredConstructorElement = new AutowiredConstructorElement();
                    autowiredConstructorElement.setConstructor(constructor);
                    if(constructor.getDeclaredAnnotation(Autowired.class).required()){
                        autowiredConstructorElement.setRequired(true);
                    }
                    else {
                        autowiredConstructorElement.setRequired(false);
                    }
                    Field[] fields1 = clazz.getDeclaredFields();
                    Class<?>[] classes = constructor.getParameterTypes();

                    //我怕这里无法找到，所以写一个标记
                    int i = 0;
                    for (Field field : fields1) {
                        for(Class<?> aClass : classes) {

                            //此处要使构造方法变量类型，该类内字段类型名，BEANDEFINITION_MAP内存储的类名一致才算拥有这个自动注入的字段
                            if (field.getName().equals(BEANDEFINITION_MAP.get(aClass.getName()).getClassName())) {
                                autowiredConstructorElement.setField(field);
                                i++;
                            }
                        }
                    }
                    if(i == 0){
                        System.out.println("查找方法自动注入时，未找到方法对应的变量名:"+classes.toString());
                    }
                    currElements.add(autowiredConstructorElement);
                }
            }

            INJECTIONELEMENT_MAP.put(entry.getKey().toLowerCase(Locale.ROOT),currElements);
        }
    }
}

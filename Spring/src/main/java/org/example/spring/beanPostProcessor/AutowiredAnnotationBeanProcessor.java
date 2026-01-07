package org.example.spring.beanPostProcessor;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Order;
import org.example.spring.beanFactoryPostProcessor.PriorityOrdered;
import org.example.spring.context.beanFactory.*;
import org.example.spring.informationEntity.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//处理@Autowired注解扫描的beanPostProcessor
public class AutowiredAnnotationBeanProcessor implements MergedBeanDefinitionPostProcessor, PriorityOrdered {
    Map<String, List<AutoElement>> injectionElement_MAP = new HashMap<>();

    @Override
    public int getOrder() {
        return 9;
    }

    @Deprecated
    public static BeanDefinition asBeanDefinitionAdd() {
        BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition();
        beanDefinition.setClassName(AutowiredAnnotationBeanProcessor.class.getSimpleName());
        beanDefinition.setClazz(AutowiredAnnotationBeanProcessor.class);
        beanDefinition.setScope("singleton");
        return beanDefinition;
    }

    /// 旧的扫描逻辑
    @Deprecated
    //ps：0为被注入的字段，1为注入的类
//    public void doAutowiredAnnotationScan(Map<String, BeanDefinition> beanDefinitionMap) {
//        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
//            List<AutoElement> currElements = new ArrayList<>(); //内部存储着当0的字段
//
//            //得到每个可能为0的类对象
//            Class<?> clazz = entry.getValue().getClazz();
//
//            //先字段扫描
//            Field[] fields = clazz.getDeclaredFields();
//            for (Field field : fields) {
//                if (field.isAnnotationPresent(Autowired.class)) {
//                    AutowiredFieldElement autowiredFieldElement = new AutowiredFieldElement();
//                    autowiredFieldElement.setField(field);
//                    if (field.getAnnotation(Autowired.class).required()) {
//                        autowiredFieldElement.setRequired(true);
//                    } else {
//                        autowiredFieldElement.setRequired(false);
//                    }
//                    currElements.add(autowiredFieldElement);
//                }
//            }
//
//            //再对方法进行扫描
//            Method[] methods = clazz.getDeclaredMethods();
//            for (Method method : methods) {
//                if (method.isAnnotationPresent(Autowired.class)) {
//                    AutowiredMethodElement autowiredMethodElement = new AutowiredMethodElement();
//                    autowiredMethodElement.setMethod(method);
//                    if (method.getAnnotation(Autowired.class).required()) {
//                        autowiredMethodElement.setRequired(true);
//                    } else {
//                        autowiredMethodElement.setRequired(false);
//                    }
//
//                    String fieldName = method.getDeclaringClass().getSimpleName().substring(3);
//                    try {
//                        Field field1 = clazz.getDeclaredField(fieldName);
//                        autowiredMethodElement.setField(field1);
//                    } catch (NoSuchFieldException e) {
//                        throw new RuntimeException(e);  //此处有一个抛出异常，与我之前的思路相同,因此不用再继续判断
//                    }
//                    currElements.add(autowiredMethodElement);
//                }
//            }
//
//            //还有构造方法
//            Constructor[] constructors = clazz.getConstructors();
//            for (Constructor constructor : constructors) {
//                if (constructor.isAnnotationPresent(Autowired.class)) {
//                    AutowiredConstructorElement autowiredConstructorElement = new AutowiredConstructorElement();
//                    autowiredConstructorElement.setConstructor(constructor);
//                    if (constructor.getDeclaredAnnotation(Autowired.class).required()) {
//                        autowiredConstructorElement.setRequired(true);
//                    } else {
//                        autowiredConstructorElement.setRequired(false);
//                    }
//
//                    String[] parameterNames = getParametersName(constructor.getParameters());
//                    for (String fieldName : parameterNames) {
//                        try {
//                            Field field1 = clazz.getDeclaredField(fieldName);
//                            AutowiredConstructorElement tempAutoCE = autowiredConstructorElement.clone();
//                            tempAutoCE.setField(field1);
//                            currElements.add(tempAutoCE);
//                        } catch (NoSuchFieldException e) {
//                            throw new RuntimeException(e);  //此处有一个抛出异常，与我之前的思路相同,因此不用再继续判断
//                        }
//                    }
//                }
//            }
//            if (!currElements.isEmpty()) {
//                entry.getValue().addAllAutoElement(currElements);
//                injectionElement_MAP.put(entry.getKey(), currElements);
//            }
//        }
//    }

    /// 用于实例化后的扫描，继承MergedBeanDefinitionPostProcessor就是为了在此处进行调用
    @Override
    public void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        doAutowiredAnnotationScan(beanDefinition,beanType,beanName);
    }
    private void doAutowiredAnnotationScan(BeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        List<AutoElement> currElements = new ArrayList<>(); //内部存储着当0的字段

        //先字段扫描
        Field[] fields = beanType.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                AutowiredFieldElement autowiredFieldElement = new AutowiredFieldElement();
                autowiredFieldElement.setField(field);
                if (field.getAnnotation(Autowired.class).required()) {
                    autowiredFieldElement.setRequired(true);
                }
                currElements.add(autowiredFieldElement);
            }
        }

        //再对方法进行扫描
        Method[] methods = beanType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Autowired.class)) {
                AutowiredMethodElement autowiredMethodElement = new AutowiredMethodElement();
                autowiredMethodElement.setMethod(method);
                if (method.getAnnotation(Autowired.class).required()) {
                    autowiredMethodElement.setRequired(true);
                }
                String fieldName = method.getDeclaringClass().getSimpleName().substring(3);
                try {
                    Field field1 = beanType.getDeclaredField(fieldName);
                    autowiredMethodElement.setField(field1);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);  //此处有一个抛出异常，与我之前的思路相同,因此不用再继续判断
                }
                currElements.add(autowiredMethodElement);
            }
        }

        //还有构造方法
        Constructor<?>[] constructors = beanType.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                AutowiredConstructorElement autowiredConstructorElement = new AutowiredConstructorElement();
                autowiredConstructorElement.setConstructor(constructor);
                if (constructor.getDeclaredAnnotation(Autowired.class).required()) {
                    autowiredConstructorElement.setRequired(true);
                }

                String[] parameterNames = getParametersName(constructor.getParameters());
                for (String fieldName : parameterNames) {
                    try {
                        Field field1 = beanType.getDeclaredField(fieldName);
                        AutowiredConstructorElement tempAutoCE = autowiredConstructorElement.clone();
                        tempAutoCE.setField(field1);
                        currElements.add(tempAutoCE);
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);  //此处有一个抛出异常，与我之前的思路相同,因此不用再继续判断
                    }
                }
            }
        }
        if (!currElements.isEmpty()) {
            beanDefinition.addAllAutoElement(currElements);
            /// 这个表好像被我废弃了，忘了有没有用了
            injectionElement_MAP.put(beanName, currElements);
        }
    }

    private String[] getParametersName(Parameter[] parameters) {
        String[] parametersNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parametersNames[i] = parameters[i].getName();
        }
        return parametersNames;
    }
}
package org.example.spring.beanPostProcessor;

import org.example.spring.annotation.Autowired;
import org.example.spring.context.beanFactory.*;
import org.example.spring.informationEntity.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//处理自动注入的beanPostProcessor
public class AutowiredAnnotationBeanProcessor implements SmartInstantiationAwareBeanPostProcessor, SmartInitializationAwareBeanPostProcessor {
    Map<String, List<AutoElement>> injectionElement_MAP = new HashMap<>();
    final private SingletonBeanRegistry beanRegistry;
    final private BeanDefinitionRegistry beanDefinitionRegistry;

    public AutowiredAnnotationBeanProcessor(SingletonBeanRegistry beanRegistry, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanRegistry = beanRegistry;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public AutowiredAnnotationBeanProcessor(DefaultListableBeanFactory factory){
        this.beanRegistry = factory;
        this.beanDefinitionRegistry = factory;
    }

    public BeanDefinition addBeanPostProcessor() {
        BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition();
        beanDefinition.setClassName(AutowiredAnnotationBeanProcessor.class.getSimpleName());
        beanDefinition.setClazz(AutowiredAnnotationBeanProcessor.class);
        beanDefinition.setScope("singleton");
        return beanDefinition;
    }

    //ps：0为被注入的字段，1为注入的类
    public void doAutowiredAnnotationScan(Map<String, BeanDefinition> beanDefinitionMap) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            List<AutoElement> currElements = new ArrayList<>(); //内部存储着当0的字段

            //得到每个可能为0的类对象
            Class<?> clazz = entry.getValue().getClazz();

            //先字段扫描
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    AutowiredFieldElement autowiredFieldElement = new AutowiredFieldElement();
                    autowiredFieldElement.setField(field);
                    if (field.getAnnotation(Autowired.class).required()) {
                        autowiredFieldElement.setRequired(true);
                    } else {
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
                    if (method.getAnnotation(Autowired.class).required()) {
                        autowiredMethodElement.setRequired(true);
                    } else {
                        autowiredMethodElement.setRequired(false);
                    }

                    String fieldName = method.getDeclaringClass().getSimpleName().substring(3);
                    try {
                        Field field1 = clazz.getDeclaredField(fieldName);
                        autowiredMethodElement.setField(field1);
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);  //此处有一个抛出异常，与我之前的思路相同,因此不用再继续判断
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
                    if (constructor.getDeclaredAnnotation(Autowired.class).required()) {
                        autowiredConstructorElement.setRequired(true);
                    } else {
                        autowiredConstructorElement.setRequired(false);
                    }

                    String[] parameterNames = getParametersName(constructor.getParameters());
                    for (String fieldName : parameterNames) {
                        try {
                            Field field1 = clazz.getDeclaredField(fieldName);
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
                entry.getValue().addAllAutoElement(currElements);
                injectionElement_MAP.put(entry.getKey(), currElements);
            }
        }
    }


    public Object doAutowiredAnnotationInjection(Object target) {
        String targetName = target.getClass().getSimpleName();   //0的类名
        BeanDefinition tbd = beanDefinitionRegistry.getBeanDefinition(targetName);//0的beanDefinition
        Map<AutoElement, Boolean> autoElementMap = tbd.getAutoElementMap(); //0们以及是否被注入


        if (autoElementMap != null) {
            for (Map.Entry<AutoElement, Boolean> autoElement : autoElementMap.entrySet()) { //遍历每一个0
                //先把0解包出来
                Field field = autoElement.getKey().getField(); //把0拆出来
                field.setAccessible(true);
                String fieldName = field.getType().getSimpleName(); //0的名字
                BeanPostProcessor proxyProcessor =beanRegistry.getSingleton("ProxyBeanPostProcessor") != null ? (BeanPostProcessor) beanRegistry.getSingleton("ProxyBeanPostProcessor") : new ProxyBeanPostProcessor(beanDefinitionRegistry);//后边循环依赖用的着
                Object oneBean = beanRegistry.getEarlyBean(fieldName) != null ? beanRegistry.getEarlyBean(fieldName) : beanRegistry.getSingleton(fieldName); //把1从单例池中掏出来

                //单例在1，2层都找不到，则进入依赖循环
                //多态在1，2层都找不到，则直接创建一个然后aop回去
                if (oneBean != null) {
                    try {
                        field.set(target, oneBean);
                        autoElement.setValue(true);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else if (beanDefinitionRegistry.containsBeanDefinition(fieldName)) {
                    BeanDefinition bd = beanDefinitionRegistry.getBeanDefinition(fieldName);
                    if (bd.getScope().equals("prototype")) {
                        try {
                            oneBean = bd.getClazz().getConstructor().newInstance();
                            field.set(target, proxyProcessor.postProcessAfterInitialization(oneBean, fieldName));
                            autoElement.setValue(true);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        ObjectFactory<Object> factory = (abstractFactory) -> {
                            //我应该获取一个需要被注入的实例，无需依赖注入，并且如果有aop我应该去实现aop。。。
                            Object bean = abstractFactory.instantiationBean(bd);//1的实例
                            return proxyProcessor.postProcessAfterInitialization(bean, fieldName);
                        };
                        beanRegistry.addFactory(fieldName, factory);
                    }
                }
            }
        }
        return target;
    }

    //拿来扫描依赖注入注解的
    @Override
    public void applyBeforeInstantiationMethod() {
        doAutowiredAnnotationScan(beanDefinitionRegistry.getBeanDefinitionMap());
    }

    //拿来运行依赖注入的
    @Override
    public Object applyBeforeInitializationMethod(Object bean) {
        return doAutowiredAnnotationInjection(bean);
    }

    private String[] getParametersName(Parameter[] parameters) {
        String[] parametersNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parametersNames[i] = parameters[i].getName();
        }
        return parametersNames;
    }
}
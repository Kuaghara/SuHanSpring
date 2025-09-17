package org.example.spring.beanPostProcessor;

import org.example.spring.Annotation.Autowired;
import org.example.spring.context.BeanFactory.AbstractDefaultListableBeanFactory;
import org.example.spring.context.BeanFactory.BeanFactory;
import org.example.spring.context.BeanFactory.DefaultListableBeanFactory;
import org.example.spring.create.CircularDependency;
import org.example.spring.create.ObjectFactory;
import org.example.spring.informationEntity.*;

import java.lang.reflect.*;
import java.util.*;


import static org.example.spring.context.BeanFactory.AbstractDefaultListableBeanFactory.instantiationBean;
import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.*;



//处理自动注入的beanPostProcessor
public class AutowiredAnnotationBeanProcessor implements SmartInstantiationAwareBeanPostProcessor,SmartInitializationAwareBeanPostProcessor {
    Map<String, List<AutoElement>> injectionElement_MAP = new HashMap<>();
    private Map<Object, Class<?>> creatingObject = new HashMap<>();//目前还没啥用

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
                    for(String fieldName : parameterNames){
                        try {
                            Field field1 = clazz.getDeclaredField(fieldName);
                            AutowiredConstructorElement tempAutoCE =  autowiredConstructorElement.clone();
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

    //此处为旧依赖注入逻辑，弃用，都实例化了竟然还传入beanDefinition，不合理,1.0版本
//    @Deprecated
//    public void doAutowiredAnnotationInjection(BeanDefinition bd) throws Exception {
//        //此处的bd为当前正在注入的beanDefinition
//        String name = bd.getClassName().toString();
//        List<AutoElement> autoElements = injectionElement_MAP.get(name);
//        if (autoElements != null) {
//            for (AutoElement autoElement : autoElements) {
//                Field field = autoElement.getField();
//                field.setAccessible(true);
//                String fieldClassName = field.getType().getSimpleName();
//                BeanPostProcessor postProcessor = new ProxyBeanPostProcessor();
//                //写的什么玩意。tnnd看不懂，新的写0和1，md简单明了
//                Object targetBean = getEarlyBean(bd.getClassName().toString());//获取当前正在注入的bean
//                Object bean = getEarlyBean(fieldClassName);//获取被注入对象
//
//                //从BEANDEFINITION_MAP中查找注入对象是否存在
//                //此处为注入对象的beanDefinition
//
//                BeanDefinition beanDefinition = beanDefinitionMap.get(fieldClassName);
//                    //判断被注入对象是不是单例
//                    if (bean != null) {
//                        try {
//                            field.set(targetBean, bean);
//                        } catch (IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    //多态找得到就找，找不到反射创建一个然后aop进去不管了
//                    else if (bd.getScope().equals("prototype")) {
//                        Object prototypeBean = singletonObjects.get(fieldClassName);
//                        if (prototypeBean != null) {
//                            try {
//                                field.set(targetBean, prototypeBean);
//                            } catch (IllegalAccessException e) {
//                                throw new RuntimeException(e);
//                            }
//                        } else {
//                            try {
//                                prototypeBean = beanDefinition.getClazz().getConstructor().newInstance();
//                                field.set(field.getDeclaringClass(),
//                                        postProcessor.postProcessAfterInitialization(prototypeBean, fieldClassName));
//                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
//                                     IllegalAccessException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                        }
//                    }
//
//                    //此处进入循环依赖部分
//                    //没有获取到对象，此时先对依赖注入进行延后到getBean时实现，之后判断是否为依赖循环
//                    //此处判断为查看源码后对ai进行提问，我想出的解决办法
//                    else {
//                        creatingObject.put(targetBean, beanDefinition.getClazz());
//                        ObjectFactory<Object> factory = new ObjectFactory<>() {
//                            @Override
//                            public Object getObject() throws Exception {
//                                //我应该获取一个需要被注入的实例，无需依赖注入，并且如果有aop我应该去实现aop。。。
//                                Object bean = AbstractDefaultListableBeanFactory.instantiationBean(beanDefinition);
//                                return postProcessor.postProcessAfterInitialization(bean, fieldClassName);
//                            }
//                        };
//                        CircularDependency.addFactory(beanDefinition.getClassName().toString(), factory);
//                    }
//
//            }
//        }
//    }

//    此为2.0版本
//    public Object doAutowiredAnnotationInjection(Object target)  {
//        String targetName = target.getClass().getSimpleName();   //0的类名
//        BeanDefinition tbd = beanDefinitionMap.get(targetName);//0的beanDefinition
//        List<AutoElement> autoElements = tbd.getAllAutoElement();
//        //List<AutoElement> autoElements = injectionElement_MAP.get(targetName); //0们
//
//        if(autoElements != null) {
//            for (AutoElement autoElement : autoElements) { //遍历每一个0
//                //先把0解包出来
//                Field field = autoElement.getField(); //把0拆出来
//                field.setAccessible(true);
//                String fieldName = field.getType().getSimpleName(); //0的名字
//                BeanPostProcessor proxyProcessor = new ProxyBeanPostProcessor();//后边循环依赖用的着
//                Object oneBean = getEarlyBean(fieldName) != null ? getEarlyBean(fieldName) : getSingletonBean(fieldName); //把1从单例池中掏出来
//
//                //单例在1，2层都找不到，则进入依赖循环
//                //多态在1，2层都找不到，则直接创建一个然后aop回去
//                if(oneBean != null){
//                    try {
//                        field.set(target, oneBean);
//                        tbd.setIsAutowired( true );//
//                        return target;
//                    }catch (IllegalAccessException e){
//                        throw new RuntimeException(e);
//                    }
//                }
//                else if(beanDefinitionMap.containsKey(fieldName)){
//                    BeanDefinition bd = beanDefinitionMap.get(fieldName);
//                    if(bd.getScope().equals("prototype")){
//                        try {
//                            oneBean = bd.getClazz().getConstructor().newInstance();
//                            field.set(target, proxyProcessor.postProcessAfterInitialization(oneBean, fieldName));
//                            return target;
//                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                                 NoSuchMethodException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                    else {
//                        creatingObject.put(target, bd.getClazz());
//                        ObjectFactory<Object> factory = new ObjectFactory<Object>() {
//                            @Override
//                            public Object getObject() throws Exception {
//                                //我应该获取一个需要被注入的实例，无需依赖注入，并且如果有aop我应该去实现aop。。。
//                                Object bean = instantiationBean(bd);//1的实例
//                                return proxyProcessor.postProcessAfterInitialization(bean, fieldName);
//                            }
//                        };
//                        CircularDependency.addFactory(fieldName, factory);
//                    }
//                }
//            }
//        }
//        return target;
//    }


    public Object doAutowiredAnnotationInjection(Object target)  {
        String targetName = target.getClass().getSimpleName();   //0的类名
        BeanDefinition tbd = beanDefinitionMap.get(targetName);//0的beanDefinition
        Map< AutoElement , Boolean > autoElementMap = tbd.getAutoElementMap(); //0们以及是否被注入


        if(autoElementMap != null) {
            for ( Map.Entry<AutoElement, Boolean> autoElement : autoElementMap.entrySet()) { //遍历每一个0
                //先把0解包出来
                Field field = autoElement.getKey().getField(); //把0拆出来
                field.setAccessible(true);
                String fieldName = field.getType().getSimpleName(); //0的名字
                BeanPostProcessor proxyProcessor = new ProxyBeanPostProcessor();//后边循环依赖用的着
                Object oneBean = getEarlyBean(fieldName) != null ? getEarlyBean(fieldName) : getSingletonBean(fieldName); //把1从单例池中掏出来

                //单例在1，2层都找不到，则进入依赖循环
                //多态在1，2层都找不到，则直接创建一个然后aop回去
                if(oneBean != null){
                    try {
                        field.set(target, oneBean);
                        autoElement.setValue( true );
                    }catch (IllegalAccessException e){
                        throw new RuntimeException(e);
                    }
                }
                else if(beanDefinitionMap.containsKey(fieldName)){
                    BeanDefinition bd = beanDefinitionMap.get(fieldName);
                    if(bd.getScope().equals("prototype")){
                        try {
                            oneBean = bd.getClazz().getConstructor().newInstance();
                            field.set(target, proxyProcessor.postProcessAfterInitialization(oneBean, fieldName));
                            autoElement.setValue( true );
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        creatingObject.put(target, bd.getClazz());
                        ObjectFactory<Object> factory = new ObjectFactory<Object>() {
                            @Override
                            public Object getObject() throws Exception {
                                //我应该获取一个需要被注入的实例，无需依赖注入，并且如果有aop我应该去实现aop。。。
                                Object bean = instantiationBean(bd);//1的实例
                                return proxyProcessor.postProcessAfterInitialization(bean, fieldName);
                            }
                        };
                        CircularDependency.addFactory(fieldName, factory);
                    }
                }
            }
        }
        return target;
    }

    //拿来扫描依赖注入注解的
    @Override
    public void applyBeforeInstantiationMethod() {
        doAutowiredAnnotationScan(beanDefinitionMap);
    }

    //拿来运行依赖注入的
    @Override
    public Object applyBeforeInitializationMethod(Object bean){
        return doAutowiredAnnotationInjection(bean);
    }

    private String[] getParametersName(Parameter[] parameters){
        String[] parametersNames = new String[parameters.length];
        for (int i = 0 ; i < parameters.length ; i++ ){
            parametersNames[i] = parameters[i].getName();
        }
        return parametersNames;
    }
}
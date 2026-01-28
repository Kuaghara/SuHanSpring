package org.example.core.beanFactoryPostProcessor;

import org.example.core.annotation.*;
import org.example.core.context.ApplicationContext;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.context.reader.AnnotationBeanDefinitionReader;
import org.example.core.informationEntity.*;
import org.example.core.util.AnnotationUtil;
import org.example.core.util.BeanUtil;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationClassParser {

    Class<?> configClass;
    AnnotationBeanDefinitionReader annReader;
    ApplicationContext context;
    ClassLoader classLoader;

    public ConfigurationClassParser(AnnotationBeanDefinitionReader annReader, ApplicationContext context) {
        this.annReader = annReader;
        this.context = context;
    }

    public void parse(BeanDefinition bd, BeanDefinitionRegistry registry) {
        configClass = bd.getClazz();
        classLoader = Thread.currentThread().getContextClassLoader();
        //我感觉我可以这样就是递归所有注解，然后把所有注解全部放到List里，然后下面就判断List里有没有这个
        List<Annotation> annotations = AnnotationUtil.getAnnotationsList(configClass, new ArrayList<>());
        bd.addAllAnnotation(annotations);

        //此处应该有一个对于轻/重配置类的判断
        bd.setFullConfigurationClass(isFullConfigurationClass(configClass));

        //递归处理Component注解的
        if(AnnotationUtil.listIncludeAnnotation(annotations, Component.class)){
            //获取全部的内部类,然后扔去递归
            List<Class<?>> innerClasses = List.of(configClass.getDeclaredClasses());
            for(Class<?> innerClass : innerClasses){
                parse(new AnnotatedGenericBeanDefinition(innerClass), registry);
            }
        }

        //处理ComponentScan注解的
        if(AnnotationUtil.listIncludeAnnotation(annotations, ComponentScan.class)){
            //从此处开始对beanDefinition的解析（扫描）
            annReader.loadBeanDefinitions(configClass);
        }

        //处理Import注解的
        if(AnnotationUtil.listIncludeAnnotation(annotations, Import.class)){
            processImport(registry, annotations);
        }

        //处理方法中Bean注解的
        processBean(configClass, registry);

    }

    public void setAnReader(AnnotationBeanDefinitionReader annReader) {
        this.annReader = annReader;
    }

    private Boolean isFullConfigurationClass(Class<?> clazz){
        List<Annotation> annotations = AnnotationUtil.getAnnotationsList(clazz,new ArrayList<>());
        return AnnotationUtil.listIncludeAnnotation(annotations, Configuration.class);
    }

    //对于Import注解的的处理，我只是单纯的将其添加到BeanDefinitionRegistry中 11.2
    //1.26 对其进行添加和修改
    private void processImport( BeanDefinitionRegistry registry, List<Annotation> annotations){
        List<Class<?>> importClasses = new ArrayList<>();

        List<Import> importAnnotation = AnnotationUtil.getTargetAnnotations(annotations, Import.class);
        for(Import importAn : importAnnotation) {
            Class<?> value = importAn.value();
            if (ImportSelector.class.isAssignableFrom(value)) {
                try {
                    //获取ImportSelector实例,此处有想用工厂去创建实例的，这没工厂（
                    ImportSelector o = (ImportSelector) BeanUtil.instantiateClass(value);
                    BeanUtil.applyAware(o);
                    AnnotationMetadata annotationMetadata = new DefaultAnnotationMetadata(configClass);
                    List<String> strings = o.selectImports(annotationMetadata);
                    for (String string : strings) {
                        Class<?> clazz = classLoader.loadClass(string);
                        importClasses.add(clazz);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                //假如不是ImportSelector，那么就直接添加
                importClasses.add(value);
            }
        }

        for(Class<?> importClass : importClasses){
            BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(importClass);
            registry.registerBeanDefinition(beanDefinition.getClassName(), beanDefinition);
        }
    }

    private void processBean(Class<?> configClass, BeanDefinitionRegistry registry){
        Method[] methods = configClass.getDeclaredMethods();
        for(Method method : methods){
            List<Annotation> annotations = AnnotationUtil.getAnnotationsList(method);
            if(!AnnotationUtil.listIncludeAnnotation(annotations, Bean.class)){
                continue;
            }
            
            boolean shouldRegisterBean = true; // 默认应该注册Bean
            
            // 检查当前方法上是否有条件注解
            if(AnnotationUtil.listIncludeAnnotation(annotations, Conditional.class)){
                List<Annotation> targetAnnotations = AnnotationUtil.getTargetAnnotations(annotations, Conditional.class);
                
                for(Annotation annotation : targetAnnotations){
                    Class<? extends Condition>[] value = ((Conditional) annotation).value();
                    for(Class<? extends Condition> condition : value){
                        try {
                            Condition o = context.getBean(condition);
                            DefaultAnnotatedTypeMetadata metadata = new DefaultAnnotatedTypeMetadata(method);
                            
                            if(!o.matches(context, metadata)){
                                // 如果当前方法的条件不满足，则不注册此方法的Bean
                                shouldRegisterBean = false;
                                break;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (!shouldRegisterBean) {
                        break; // 如果已确定不注册当前方法的Bean，则跳出条件检查循环
                    }
                }
            }
            
            // 根据当前方法的条件判断结果决定是否注册Bean
            if (shouldRegisterBean) {
                // 注册Bean定义
                Class<?> returnType = method.getReturnType();
                BeanDefinition bd = new AnnotatedGenericBeanDefinition(returnType);
                bd.setBeanMethod(method);
                bd.setConfigurationClassName(configClass.getSimpleName());
                registry.registerBeanDefinition(returnType.getSimpleName(), bd);
            }
        }
    }

    //此处递归注解，返还一个包含所有内部注解的表
    //方法已被移至 AnnotationUtil
    @Deprecated
    public List<Annotation> parseAnnotation(Class<?> config, List<Annotation> annotationList){
        List<Annotation> annotations = List.of(config.getDeclaredAnnotations());

        //我认为当一个注解有且仅有Target，Retention，Documented的时候，这个注解就是元注解
        if(config.getAnnotations().length == 3 && (AnnotationUtil.listIncludeAnnotation(annotations, Target.class) && AnnotationUtil.listIncludeAnnotation(annotations ,Retention.class) && AnnotationUtil.listIncludeAnnotation(annotations,Documented.class))){
            return annotationList;
        }

        for(Annotation annotation : config.getAnnotations()){
            if(annotation instanceof Target || annotation instanceof Retention || annotation instanceof Documented){
                continue;
            }
            if (!annotationList.contains( annotation)){
                annotationList.add(annotation);
                parseAnnotation(annotation.annotationType(), annotationList);
            }
        }
        return annotationList;
    }

//    private boolean isMatch(AnnotatedTypeMetadata annotatedTypeMetadata, List<Annotation> annotationsList) throws Exception {
//        List<Conditional> targetAnnotations = AnnotationUtil.getTargetAnnotations(annotationsList, Conditional.class);
//        boolean isMatch = true;
//        for(Conditional conditional:targetAnnotations){
//            //把判断器取出来
//            Class<?> conditionClass = conditional.value()[0];
//            //实例然后进行调用
//            Condition bean =(Condition) context.getBean(conditionClass);
//            if(!bean.matches(context,annotatedTypeMetadata)){
//                isMatch = false;
//                break;
//            }
//        }
//        return isMatch;
//    }
}
package org.example.spring.beanFactoryPostProcessor;

import org.example.spring.annotation.*;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.reader.AnnotationBeanDefinitionReader;
import org.example.spring.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.util.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationClassParser {

    AnnotationBeanDefinitionReader annReader;

    public ConfigurationClassParser(AnnotationBeanDefinitionReader annReader) {
        this.annReader = annReader;
    }

    public void parse(BeanDefinition bd, BeanDefinitionRegistry registry) {
        Class<?> configClass = bd.getClazz();
        //未拥有对该配置类的注解递归查找，进行添加
        //我感觉我可以这样就是递归所有注解，然后把所有注解全部放到List里，然后下面就判断List里有没有这个
        List<Annotation> annotations = parseAnnotation(configClass, new ArrayList<>());

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
        return clazz.isAnnotationPresent(Configuration.class);
    }

    //对于Import注解的的处理，我只是单纯的将其添加到BeanDefinitionRegistry中
    private void processImport( BeanDefinitionRegistry registry, List<Annotation> annotations){
        List<Class<?>> importClasses = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Import) {
                importClasses.addAll(List.of(((Import) annotation).value()));
            }
        }

        for(Class<?> importClass : importClasses){
            BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(importClass);
            registry.registerBeanDefinition(beanDefinition.getClassName(), beanDefinition);
        }
    }

    private void processBean(Class<?> configClass, BeanDefinitionRegistry registry){
        Method[]  methods = configClass.getDeclaredMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(Bean.class)){
                Type returnType = method.getGenericReturnType();
                BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(returnType.getClass());
                registry.registerBeanDefinition(beanDefinition.getClassName(), beanDefinition);
            }
        }
    }

    //此处递归注解，返还一个包含所有内部注解的表
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
}

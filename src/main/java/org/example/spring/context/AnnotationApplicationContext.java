package org.example.spring.context;

import org.example.spring.Annotation.Import;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.context.BeanFactory.BeanFactory;
import org.example.spring.context.BeanFactory.DefaultListableBeanFactory;
import org.example.spring.context.Reader.AnnotationBeanDefinitionReader;
import org.example.spring.context.Reader.ClassPathBeanDefinitionReader;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.context.Reader.BeanDefinitionReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AnnotationApplicationContext extends DefaultListableBeanFactory implements ApplicationContext {
    private BeanDefinitionReader annotationReader;//存储读主配置类里的bean的类
    private BeanDefinitionReader classReader;//存储扫描bean的类
    private List<BeanDefinition> generatedDefinitionList = new ArrayList<>();//用于在beanDefinition生成途中进行临时存储的
    private Class<?> mainConfigClass = null;//存储主配置类


    //此初始化方法只是创造了点方法实例存储起来
    public AnnotationApplicationContext() {
        annotationReader = new AnnotationBeanDefinitionReader();
        classReader = new ClassPathBeanDefinitionReader();

    }

    //此初始化方法就是完成了扫描的工作
    public AnnotationApplicationContext(Class<?> configClass) {
        this();
        this.mainConfigClass = configClass;
        setBeanClassLoader(this.mainConfigClass.getClassLoader());
        generatedDefinitionList.addAll(annotationReader.loadBeanDefinitions(mainConfigClass));
        generatedDefinitionList.addAll(classReader.loadBeanDefinitions(mainConfigClass));
        doCreatBeanDefinitionMap(generatedDefinitionList);
        explainConfigAnnotation(mainConfigClass);//查找配置类中的@Import注解
        super.refresh();//此处回调beanFactory的refresh方法
    }

    private void doCreatBeanDefinitionMap(List<BeanDefinition> generatedDefinitionList){
        for(BeanDefinition bd : generatedDefinitionList){
            registerBeanDefinition(bd.getClassName().toString(),bd);//此处回调工厂方法把beanDefinition注册进去
        }
    }

    //用来查找@import注解
    private void explainConfigAnnotation(Class<?> configClass){
            List<Annotation> annotations = List.of(configClass.getAnnotations());
            Import importAnnotation = configClass.getAnnotation(Import.class);
            if (annotations.isEmpty()) {
                return;
            }
            if (importAnnotation == null) {
                for(Annotation annotation : annotations){
                    explainConfigAnnotation(annotation.getClass());
                }
            }
            else {
                Class< ? > [] importBeanPostProcessors = importAnnotation.value();
                for(Class clazz : importBeanPostProcessors){
                    try {
                        addBeanPostProcessor((BeanPostProcessor) clazz.getConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
    }

    @Override
    public Object getBean(String beanName)  {
        try {
            return super.getBean(beanName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

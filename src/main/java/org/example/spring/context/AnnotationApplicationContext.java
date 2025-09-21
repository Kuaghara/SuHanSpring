package org.example.spring.context;

import org.example.spring.annotation.Import;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.context.beanFactory.DefaultListableBeanFactory;
import org.example.spring.context.reader.AnnotationBeanDefinitionReader;
import org.example.spring.context.reader.BeanDefinitionReader;
import org.example.spring.context.reader.ClassPathBeanDefinitionReader;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AnnotationApplicationContext extends DefaultListableBeanFactory implements ApplicationContext {
    final private BeanDefinitionReader annotationReader;//通过配置类进行注册的读取器
    //final private BeanDefinitionReader classReader;//通过路径进行注册的读取器
    private Class<?> mainConfigClass = null;//存储主配置类
    final private DefaultListableBeanFactory factory;

    //此初始化方法只是创造了点方法实例存储起来
    public AnnotationApplicationContext() {
        factory = new DefaultListableBeanFactory();
        annotationReader = new AnnotationBeanDefinitionReader(factory);
        //classReader = new ClassPathBeanDefinitionReader();
    }

    //此初始化方法就是完成了扫描的工作
    public AnnotationApplicationContext(Class<?> configClass) {
        this();
        this.mainConfigClass = configClass;
        this.annotationReader.loadBeanDefinitions(mainConfigClass);
        explainConfigAnnotation(mainConfigClass);//查找配置类中的@Import注解
        factory.refresh();//此处调用beanFactory的refresh方法
    }

    private void doCreatBeanDefinitionMap(List<BeanDefinition> generatedDefinitionList) {
        for (BeanDefinition bd : generatedDefinitionList) {
            registerBeanDefinition(bd.getClassName(), bd);//此处回调工厂方法把beanDefinition注册进去
        }
    }

    //用来查找@import注解
    private void explainConfigAnnotation(Class<?> configClass) {
        List<Annotation> annotations = List.of(configClass.getAnnotations());
        Import importAnnotation = configClass.getAnnotation(Import.class);
        if (annotations.isEmpty()) {
            return;
        }
        if (importAnnotation == null) {
            for (Annotation annotation : annotations) {
                if (Target.class.equals(annotation.annotationType()) || Retention.class.equals(annotation.annotationType())) {
                    continue;
                }
                explainConfigAnnotation(annotation.annotationType());
            }
        } else {
            Class<?>[] importBeanPostProcessors = importAnnotation.value();
            for (Class clazz : importBeanPostProcessors) {
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
    public Object getBean(String beanName) {
        try {
            return factory.getBean(beanName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

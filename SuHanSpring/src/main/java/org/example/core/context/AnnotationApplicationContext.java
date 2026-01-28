package org.example.core.context;

import org.example.core.context.reader.AnnotationBeanDefinitionReader;
import org.example.core.context.reader.BeanDefinitionReader;
import org.example.core.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.core.informationEntity.BeanDefinition;

import java.util.List;

public class AnnotationApplicationContext extends GenericApplicationContext{

    final private BeanDefinitionReader annotationReader;//通过配置类进行注册的读取器
    //final private BeanDefinitionReader classReader;//通过路径进行注册的读取器

    //此初始化方法只是创造了点方法实例存储起来
    public AnnotationApplicationContext() {
        annotationReader = new AnnotationBeanDefinitionReader(this);
        //classReader = new ClassPathBeanDefinitionReader();
    }

    //此初始化方法就是完成了扫描的工作
    public AnnotationApplicationContext(Class<?> configClass) {
        this();
        register(configClass);
        refresh();
    }

    private void doCreatBeanDefinitionMap(List<BeanDefinition> generatedDefinitionList) {
        for (BeanDefinition bd : generatedDefinitionList) {
            registerBeanDefinition(bd.getClassName(), bd);//此处回调工厂方法把beanDefinition注册进去
        }
    }

    ///已经扔到配置类解析里了，你已经不用再继续奋斗了
    //用来查找@import注解
//    private void explainConfigAnnotation(Class<?> configClass) {
//        List<Annotation> annotations = List.of(configClass.getAnnotations());
//        Import importAnnotation = configClass.getAnnotation(Import.class);
//        if (annotations.isEmpty()) {
//            return;
//        }
//        if (importAnnotation == null) {
//            for (Annotation annotation : annotations) {
//                if (Target.class.equals(annotation.annotationType()) || Retention.class.equals(annotation.annotationType())) {
//                    continue;
//                }
//                explainConfigAnnotation(annotation.annotationType());
//            }
//        } else {
//            Class<?>[] importBeanPostProcessors = importAnnotation.value();
//            for (Class clazz : importBeanPostProcessors) {
//                try {
//                    addBeanPostProcessor((BeanPostProcessor) clazz.getConstructor().newInstance());
//                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                         NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }



    private void register(Class<?> mainConfig){
        super.registerBeanDefinition(mainConfig.getSimpleName(), new AnnotatedGenericBeanDefinition(mainConfig));
    }
}

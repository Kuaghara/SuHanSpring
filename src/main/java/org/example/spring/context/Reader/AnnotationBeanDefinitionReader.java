package org.example.spring.context.Reader;

import org.example.spring.Annotation.Bean;
import org.example.spring.beanPostProcessor.AutowiredAnnotationBeanProcessor;
import org.example.spring.beanPostProcessor.ProxyBeanPostProcessor;
import org.example.spring.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.example.spring.context.AnnotationApplicationContext.SUHANCLASSLOADER;


public class AnnotationBeanDefinitionReader implements BeanDefinitionReader {
    //对class配置类中的定义的bean进行扫描

    @Override
    public List<BeanDefinition> loadBeanDefinitions(Class<?> clazz) {
        List<BeanDefinition> generateBeanDefinition = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                try {
                    //创造bean的反射对象
                    Class<?> targetClass = SUHANCLASSLOADER.loadClass(method.getReturnType().getName());
                    BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition();
                    beanDefinition.setBeanDefinition(targetClass);
                    generateBeanDefinition.add(beanDefinition);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        AutowiredAnnotationBeanProcessor autowiredAnnotationBeanProcessor = new AutowiredAnnotationBeanProcessor();
        ProxyBeanPostProcessor proxyBeanPostProcessor = new ProxyBeanPostProcessor();
        generateBeanDefinition.add(autowiredAnnotationBeanProcessor.addBeanPostProcessor());
        generateBeanDefinition.add(proxyBeanPostProcessor.addBeanPostProcessor());

        return generateBeanDefinition;
    }

}

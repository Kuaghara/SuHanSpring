package org.example.spring.scan;

import org.example.spring.Annotation.Bean;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Method;
import java.util.List;

import static org.example.spring.SuHanApplication.SUHANCLASSLOADER;

public class ConfigClassScans {
    //对class配置类中的定义的bean进行扫描

    public static void configClassScan(Class<?> clazz, List<Object> generateBeanDefinition) {

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                try {
                    Class<?> class1 = SUHANCLASSLOADER.loadClass(method.getReturnType().getName());
                    generateBeanDefinition.add(new BeanDefinition().setClassName(class1));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }
}

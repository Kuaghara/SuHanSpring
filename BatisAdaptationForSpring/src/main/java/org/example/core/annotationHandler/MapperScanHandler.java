package org.example.core.annotationHandler;

import org.example.core.annotation.MapperScan;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.core.informationEntity.BeanDefinition;
import org.example.core.util.AnnotationUtil;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapperScanHandler {
    public Map<String, BeanDefinition> parseMapperScan(Class<?> clazz, BeanDefinitionRegistry registry, BeanDefinition bd) {
        Map<String, BeanDefinition> mapperBdm = new HashMap<>();
        List<Annotation> annonationsList = AnnotationUtil.getAnnonationsList(bd);
        for (Annotation annotation : annonationsList) {
            if (annotation.annotationType().equals(MapperScan.class)) {
                MapperScan mapperScan = (MapperScan) annotation;
                String basePackage = mapperScan.value();
                String path = basePackage.replace(".", "/");
                ClassLoader classLoader = clazz.getClassLoader();
                URL resource = classLoader.getResource(path);
                if (resource == null) {
                    throw new RuntimeException("没找到包路径" + basePackage);
                }
                File file = new File(resource.getFile());
                if (file.isDirectory()) {
                    for (File f : Objects.requireNonNull(file.listFiles())) {
                        try {
                            String fileName = f.getName();
                            if (!fileName.endsWith(".class")) {
                                continue;
                            }
                            String simpleClassName = fileName.substring(0, fileName.length() - 6);
                            String fullClassName = basePackage + "." + simpleClassName;
                            Class<?> mapper = classLoader.loadClass(fullClassName);
                            if (mapper.isInterface()) {
                                BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(mapper);
                                registry.registerBeanDefinition(mapper.getSimpleName(), beanDefinition);
                                mapperBdm.put(mapper.getSimpleName(), beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return mapperBdm;
    }
}

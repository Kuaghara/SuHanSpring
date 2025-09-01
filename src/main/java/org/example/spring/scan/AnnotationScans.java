package org.example.spring.scan;

import org.example.spring.Annotation.ComponentScan;
import org.example.spring.Annotation.Lazy;
import org.example.spring.Annotation.Scope;
import org.example.spring.informationEntity.BeanDefinition;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static org.example.spring.SuHanApplication.SUHANCLASSLOADER;

public class AnnotationScans {
    public static void annotationScan(Class<?> clazz,List<Object> generateBeanDefinition)  {
        ComponentScan scan = clazz.getDeclaredAnnotation(ComponentScan.class);

        String basePackage = scan.value();
        String packagePath = basePackage.replace(".", "/");


        URL url = SUHANCLASSLOADER.getResource(packagePath);

        //将获取到的文件路径转换成File类型
        File file = null;
        if (url != null) {
            file = new File(url.getFile());
        }
        if (file != null && file.isDirectory()) {/*是目录就继续*/
            for (File f : Objects.requireNonNull(file.listFiles())) {//遍历每一个文件

                //将File类型对象变换为Class类型对象
                String relativePath = f.getAbsolutePath().substring(url.getFile().length());
                String classPath = basePackage + "." + relativePath.replace(File.separator, ".")
                        .replace(".class", "");

                //获取文件的类
                Class<?> class1 = null;
                try {
                    class1 = SUHANCLASSLOADER.loadClass(classPath);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                //创建beanDefinition对象
                try {
                    BeanDefinition beanDefinition = new BeanDefinition();

                    setBeanDefinition(class1, beanDefinition);

                    generateBeanDefinition.add(beanDefinition);
                } catch (Exception e) {
                    throw new RuntimeException("发现了两个重复的bean：" + class1);
                }

            }
        }

    }

    public static void setBeanDefinition(Class<?> class1, BeanDefinition beanDefinition) {
        //查找Scope注解
        if(class1.isAnnotationPresent(Scope.class)){
            Scope declaredAnnotation = class1.getDeclaredAnnotation(Scope.class);
            beanDefinition.setScope(declaredAnnotation.value());
        }
        else {
            beanDefinition.setScope("singleton");
        }

        //查找Lazy注解
        if(class1.isAnnotationPresent(Lazy.class)){
            Lazy declaredAnnotation = class1.getDeclaredAnnotation(Lazy.class);
            beanDefinition.setLazy(declaredAnnotation.value());
        }
        else {
            beanDefinition.setLazy("false");
        }
        beanDefinition.setClassName(class1);
    }

}

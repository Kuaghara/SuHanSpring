package org.example.core.context.reader;

import org.example.core.annotation.Bean;
import org.example.core.annotation.Component;
import org.example.core.annotation.ComponentScan;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.core.informationEntity.BeanDefinition;
import org.example.core.util.AnnotationUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AnnotationBeanDefinitionReader implements BeanDefinitionReader {
    final private BeanDefinitionRegistry beanDefinitionRegistry;
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    //对class配置类中的定义的bean进行扫描


    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void loadBeanDefinitions(Class<?> clazz) {
        //传入一个配置类和一个容器，预想是传一个beanFactory
        List<BeanDefinition> generateBeanDefinitions = new ArrayList<>(doLocationLoadBeanDefinitions(clazz));

        beanDefinitionRegistry.registerAllBeanDefinition(generateBeanDefinitions);
    }

    @Deprecated
    private List<BeanDefinition> doAnnotationLoadBeanDefinitions(Class<?> clazz) {
        List<BeanDefinition> generateBeanDefinition = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {


            }
        }
        return generateBeanDefinition;
    }


    private List<BeanDefinition> doLocationLoadBeanDefinitions(Class<?> clazz) {
        List<Annotation> annonationsList = AnnotationUtil.getAnnotationsList(clazz, new ArrayList<>());
        ComponentScan scan = null;
        if (AnnotationUtil.listIncludeAnnotation(annonationsList, ComponentScan.class)) {
            scan = AnnotationUtil.getAnnotationFromList(annonationsList, ComponentScan.class);
        }
        String basePackage = scan.value();
        //如果没有指定包名，就默认为当前包名，此处为对springboot注解的实现
        if (basePackage.isEmpty()) {
            basePackage = clazz.getPackage().getName();
        }
        String location = basePackage.replace(".", "/");
        ClassLoader classLoader = clazz.getClassLoader();
        List<BeanDefinition> generateBeanDefinition = new ArrayList<>();
        URL url = classLoader.getResource(location);
        File file = null;
        if (url != null) {
            file = new File(url.getFile());
        }

        // 递归扫描包中的类
        scanPackage(file, location, classLoader, generateBeanDefinition);

        return generateBeanDefinition;
    }

    private void scanPackage(File file, String location, ClassLoader classLoader, List<BeanDefinition> generateBeanDefinition) {
        if (file == null) {
            throw new RuntimeException("未找到指定路径下的文件");
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    scanPackage(f, location, classLoader, generateBeanDefinition);
                }
            }
        } else if (file.isFile() && file.getName().endsWith(".class")) {
            try {
                // 使用Paths工具类进行路径处理
                Path filePath = Paths.get(file.getCanonicalPath());
                String fullClassName = "";

                // 1. 找到 "org" 在路径组件中的索引位置
                int startIndex = -1;
                for (int i = 0; i < filePath.getNameCount(); i++) {
                    if (filePath.getName(i).toString().equals("org")) {
                        startIndex = i;
                        break;
                    }
                }

                if (startIndex != -1) {
                    // 2. 截取从 "org" 开始到结尾的部分
                    Path packagePath = filePath.subpath(startIndex, filePath.getNameCount());

                    // 3. 将路径转为字符串，去掉 .class，替换分隔符
                    fullClassName = packagePath.toString()
                            .replace(".class", "")
                            .replace(filePath.getFileSystem().getSeparator(), ".");

                }


                // 尝试加载类，如果失败则跳过（关键修复）
                Class<?> clazz;
                try {
                    clazz = classLoader.loadClass(fullClassName);
                } catch (ClassNotFoundException e) {
                    // 跳过无法加载的类，不抛出异常
                    System.err.println("警告: 无法加载类 " + fullClassName + ", 跳过该文件");
                    return;
                }

                List<Annotation> annotations = AnnotationUtil.getAnnotationsList(clazz, new ArrayList<>());
                if (AnnotationUtil.listIncludeAnnotation(annotations, Component.class)) {
                    AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(clazz);
                    generateBeanDefinition.add(beanDefinition);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}

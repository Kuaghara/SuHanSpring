package org.example.spring.context.reader;

import org.example.spring.annotation.Bean;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.ComponentScan;
import org.example.spring.beanPostProcessor.AutowiredAnnotationBeanProcessor;
import org.example.spring.beanPostProcessor.ProxyBeanPostProcessor;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.DefaultListableBeanFactory;
import org.example.spring.context.beanFactory.SingletonBeanRegistry;
import org.example.spring.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.informationEntity.ScannedGenericBeanDefinition;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AnnotationBeanDefinitionReader implements BeanDefinitionReader {
    final private BeanDefinitionRegistry beanDefinitionRegistry;
    final private SingletonBeanRegistry singletonBeanRegistry;
    //对class配置类中的定义的bean进行扫描


    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry, SingletonBeanRegistry singletonBeanRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        this.singletonBeanRegistry = singletonBeanRegistry;
    }

    public AnnotationBeanDefinitionReader(DefaultListableBeanFactory factory){
        this.beanDefinitionRegistry = factory;
        this.singletonBeanRegistry = factory;
    }

    @Override
    public void loadBeanDefinitions(Class<?> clazz) { //传入一个配置类和一个容器，预想是传一个beanFactory
        List<BeanDefinition> generateBeanDefinition = doAnnotationLoadBeanDefinitions(clazz);
        generateBeanDefinition.addAll(doLocationLoadBeanDefinitions(clazz));

        //添加beanFactoryPostProcessor
        AutowiredAnnotationBeanProcessor autowiredAnnotationBeanProcessor = new AutowiredAnnotationBeanProcessor(singletonBeanRegistry,beanDefinitionRegistry);
        //proxy在写完import之后会单独写一个@EnableProxy注解来导入
        ProxyBeanPostProcessor proxyBeanPostProcessor = new ProxyBeanPostProcessor(beanDefinitionRegistry);
        generateBeanDefinition.add(autowiredAnnotationBeanProcessor.addBeanPostProcessor());
        generateBeanDefinition.add(proxyBeanPostProcessor.addBeanPostProcessor());

        beanDefinitionRegistry.registerAllBeanDefinition(generateBeanDefinition);
    }

    private List<BeanDefinition> doAnnotationLoadBeanDefinitions(Class<?> clazz) {
        List<BeanDefinition> generateBeanDefinition = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                    //创造bean的反射对象
                    Class<?> targetClass = method.getReturnType().getClass();
                    BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition();
                    beanDefinition.setBeanDefinition(targetClass);
                    generateBeanDefinition.add(beanDefinition);
            }
        }
        return generateBeanDefinition;
    }


    private List<BeanDefinition> doLocationLoadBeanDefinitions(Class<?> clazz) {
        ComponentScan scan = clazz.getDeclaredAnnotation(ComponentScan.class);
        String basePackage = scan.value();
        String location = basePackage.replace(".", "/");
        ClassLoader classLoader = clazz.getClassLoader();
        List<BeanDefinition> generateBeanDefinition = new ArrayList<>();
        URL url = classLoader.getResource(location);

        //将获取到的文件路径转换成File类型
        File file = null;
        if (url != null) {
            file = new File(url.getFile());
        }
        if (file != null && file.isDirectory()) {//是目录就继续
            for (File f : Objects.requireNonNull(file.listFiles())) {//遍历每一个文件
                //将File类型对象变换为Class类型对象
                String classPath = f.getPath().substring(url.getFile().length());
                String className = classPath.substring(0, classPath.length() - 6);
                String finallyClassPath = location.replace("/", ".") + "." + className;

                //获取文件的类
                Class<?> class1 = null;
                try {
                    class1 = classLoader.loadClass(finallyClassPath);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                if (class1.isAnnotationPresent(Component.class)) {
                    //创建beanDefinition对象
                    try {
                        BeanDefinition beanDefinition = new ScannedGenericBeanDefinition();
                        beanDefinition.setBeanDefinition(class1);
                        generateBeanDefinition.add(beanDefinition);
                    } catch (Exception e) {
                        throw new RuntimeException("发现了两个重复的bean：" + class1);
                    }
                }
            }
        }
        return generateBeanDefinition;
    }
}

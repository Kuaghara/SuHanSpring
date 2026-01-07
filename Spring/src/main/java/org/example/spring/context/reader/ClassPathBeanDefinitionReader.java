package org.example.spring.context.reader;

import org.example.spring.context.beanFactory.DefaultListableBeanFactory;

import java.util.ArrayList;


//对@ComponentScan注解进行扫描
public class ClassPathBeanDefinitionReader implements BeanDefinitionReader {

    //@Override
    public void loadBeanDefinitions(Class<?> clazz, DefaultListableBeanFactory registry) {
//        ComponentScan scan = clazz.getDeclaredAnnotation(ComponentScan.class);
//        String basePackage = scan.value();
//        String packagePath = basePackage.replace(".", "/");
//        return loadBeanDefinitions(packagePath);
    }

    //@Override
    public void loadBeanDefinitions(String location, DefaultListableBeanFactory registry) {
//        List<BeanDefinition> generateBeanDefinition = new ArrayList<>();
//        URL url = SUHANCLASSLOADER.getResource(location);
//
//        //将获取到的文件路径转换成File类型
//        File file = null;
//        if (url != null) {
//            file = new File(url.getFile());
//        }
//        if (file != null && file.isDirectory()) {//是目录就继续
//            for (File f : Objects.requireNonNull(file.listFiles())) {//遍历每一个文件
//                //将File类型对象变换为Class类型对象
//                String classPath = f.getPath().substring(url.getFile().length());
//                String className = classPath.substring(0, classPath.length() - 6);
//                String finallyClassPath = location.replace("/", ".") + "." + className;
////                String relativePath = f.getAbsolutePath().substring(url.getFile().length());
////                String classPath = location + "." + relativePath.replace(File.separator, ".")
////                        .replace(".class", "");
//
//                //获取文件的类
//                Class<?> class1 = null;
//                try {
//                    class1 = SUHANCLASSLOADER.loadClass(finallyClassPath);
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//
//                //创建beanDefinition对象
//                try {
//                    BeanDefinition beanDefinition = new ScannedGenericBeanDefinition();
//                    beanDefinition.setBeanDefinition(class1);
//                    generateBeanDefinition.add(beanDefinition);
//                } catch (Exception e) {
//                    throw new RuntimeException("发现了两个重复的bean：" + class1);
//                }
//            }
//        }
//        return generateBeanDefinition;
    }
}

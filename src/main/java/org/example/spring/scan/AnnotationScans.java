package org.example.spring.scan;

import org.example.spring.Annotation.ComponentScan;
import org.example.spring.informationEntity.BeanDefinition;

import java.io.File;
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
                    Object b =new BeanDefinition().setClassName(class1);
                    generateBeanDefinition.add(b);
                } catch (Exception e) {
                    throw new RuntimeException("发现了两个重复的bean：" + class1);
                }
                //此处为原来的逻辑，生成beanDefinition对象并将其放在map中，生成单例bean并放在单例池中
                //更新后将步骤拆分细化，此处先进行路径的注释扫描
                //相关细分代码编写完毕后便会将其删除


//                BeanDefinition beanDefinition = new BeanDefinition();
//                //保存类名
//
//                Component component = classLoader.getDeclaredAnnotation(Component.class);
//                //为后方单例模式名字创建注解对象
//
//                String beanName;
//                if(component.value().equals("")){
//                    beanName = classLoader.getSimpleName();
//                }
//                else {
//                    beanName = component.value();
//                }
//                beanDefinition.setClassName(beanName);
//
//                if (classLoader.isAnnotationPresent(Scope.class)) {
//                    Scope scope = classLoader.getDeclaredAnnotation(Scope.class);
//                    String scopeValue = scope.value();
//
//                    //判断为原型模式，将原型保存
//                    if (scopeValue.equals("prototype")) {
//                        beanDefinition.setClazz(classLoader);
//                        beanDefinition.setScope("prototype");
//
//                    } else {
//                        beanDefinition.setClazz(classLoader);
//                        singletonbeanMap.put(beanName,createSingletonBean(beanDefinition));
//
//                    }
//                } else {
//                    beanDefinition.setClazz(classLoader);
//                    singletonbeanMap.put(beanName, createSingletonBean(beanDefinition));
//
//                }
//
//                beandefinitionMap.put(beanName, beanDefinition);
//
            }
        }
    }

}

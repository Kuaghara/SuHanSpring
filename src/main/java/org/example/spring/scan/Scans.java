package org.example.spring.scan;

import org.example.spring.Annotation.ComponentScan;
import org.example.spring.Annotation.Configuration;
import org.example.spring.Annotation.Lazy;
import org.example.spring.Annotation.Scope;
import org.example.spring.BeanDefinition;

import java.text.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.spring.SuHanApplication.beandefinitionMap;
import static org.example.spring.scan.AnnotationBeanNameGenerator.generateName;
import static org.example.spring.scan.AnnotationScans.AnnotationScan;
import static org.example.spring.scan.ConfigClassScans.ConfigClassScan;

public class Scans {

    //创建扫描完的beanDefinition List
    public static List<Object> generateBeanDefinition = new ArrayList<>();
    public static void scan(Class<?> clazz)  {
        //读取注册的bean(存在mainConfig中的+使用component注解的)->创建bean
        if (clazz.isAnnotationPresent(Configuration.class)) {

            //根据提供的路径进行一次扫描
            if (clazz.isAnnotationPresent(ComponentScan.class)) AnnotationScan(clazz,generateBeanDefinition);

            //对配置类中自带的bean进行扫描
            ConfigClassScan(clazz,generateBeanDefinition);

            //此时已获得包含所有扫描出来的beanDefinition的List
            //接下来创造beanDefinition Map
            for(Object beanDefinition : generateBeanDefinition){
                String beanName = generateName(beanDefinition);
                beandefinitionMap.computeIfAbsent(beanName, k -> (BeanDefinition) beanDefinition);
            }

            //接下来配置每个definition
           for (Map.Entry<String,BeanDefinition> entry : beandefinitionMap.entrySet()){

               //先获取一个beanDefinition对象
               BeanDefinition bd = entry.getValue();
               try {

                   //然后获取一个类的class对象
                   //!此处可能为一个bug点，因为会自动调用初始化方法，比较担心,并且substring内部的数字是我观察得出的
                   Class<?> class2 = Class.forName(bd.getClassName().toString().substring(6));


               //重新修改clazz及name属性
               bd.setClazz(class2);
               bd.setClassName(entry.getKey());

               // 开始对添加的注释属性进行添加,先判断是否为单例
              if(class2.isAnnotationPresent(Scope.class)){
                  String scopeValue = class2.getDeclaredAnnotation(Scope.class).value();
                  entry.getValue().setScope(scopeValue);
              }

              //再判断是否是懒加载
              if(class2.isAnnotationPresent(Lazy.class)){
                  String lazyValue = class2.getDeclaredAnnotation(Lazy.class).value();
                  entry.getValue().setLazy(lazyValue);
              }
              
              //目前就只有这两个注解，就先添加这两个吧
               }
               catch (ClassNotFoundException e) {
                   throw new RuntimeException(e);
               }
           }
        }
        else {
            throw new NullPointerException("不能传入一个没有扫描路径/定义bean的配置类");
        }
    }
}

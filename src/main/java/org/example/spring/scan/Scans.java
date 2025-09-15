package org.example.spring.scan;

import org.example.spring.Annotation.ComponentScan;
import org.example.spring.Annotation.Configuration;
import org.example.spring.beanPostProcessor.ProxyBeanPostProcessor;
import org.example.spring.informationEntity.BeanDefinition;

import java.util.ArrayList;
import java.util.List;



import static org.example.spring.create.CreatBeanDefinitions.creatBeanDefinitionMap;


import static org.example.spring.scan.AutoWiredScans.autoWiredScan;


//@Deprecated
//public class Scans {
//
//    //创建扫描完的beanDefinition List
//    public static List<Object> GENERATEDEFINITION_LIST = new ArrayList<>();
//    public static void scan(Class<?> clazz)  {
//
//        //读取注册的bean(存在mainConfig中的+使用component注解的)->创建bean
//        if (clazz.isAnnotationPresent(Configuration.class)) {
//
//            //根据提供的路径进行一次扫描
//            if (clazz.isAnnotationPresent(ComponentScan.class))
//            { annotationScan(clazz, GENERATEDEFINITION_LIST);}
//
//            //对配置类中自带的bean进行扫描
//            configClassScan(clazz, GENERATEDEFINITION_LIST);
//
//            //此时已获得包含所有扫描出来的beanDefinition的List
//            //接下来创造beanDefinition Map
//            creatBeanDefinitionMap(clazz, GENERATEDEFINITION_LIST);
//
//            //再对beanDefinitionMap进行扫描，查找出所有包含@Autowired的属性或者方法，为将来的属性注入做准备
//            autoWiredScan(BEANDEFINITION_MAP);//将其扔入beanPostProcessor中,当实例化前的一个实现吧
//
//            for(BeanDefinition bd : BEANDEFINITION_MAP.values()){ //这个也是，同上
//                ProxyBeanPostProcessor.registerAdvisor(bd);
//            }
//
//
//        }
//        else {
//            throw new NullPointerException("不能传入一个没有扫描路径/定义bean的配置类");
//        }
//    }
//}

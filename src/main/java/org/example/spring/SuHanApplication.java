package org.example.spring;

import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.spring.create.CreatBeans.creatBean;
import static org.example.spring.create.Creates.creat;
import static org.example.spring.scan.Scans.scan;


public class SuHanApplication {
    Class<?> mainConfig;
    public static Map<String, BeanDefinition> BEANDEFINITION_MAP = new HashMap<>();
    public static Map<String, Object> SINGLETONBEAN_MAP = new HashMap<>();
    public static ClassLoader SUHANCLASSLOADER = null;
    public static Map<String, List<AutoElement>> INJECTIONELEMENT_MAP = new HashMap<>();
    public static List<Class> AOP_LIST = new ArrayList<>();


    public SuHanApplication(Class<?> clazz) {
        setClassLoader(clazz.getClassLoader());
        this.mainConfig = clazz;
        scan(clazz);
        creat();

    }


    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = BEANDEFINITION_MAP.get(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException(beanName + "不存在");
        } else {
            if (beanDefinition.getScope().equals("singleton")) {
                return SINGLETONBEAN_MAP.get(beanName);
            } else {
                return creatBean(beanDefinition);
            }
        }
    }

    public static void setClassLoader(ClassLoader classLoader) {
        SUHANCLASSLOADER = classLoader;
    }

}

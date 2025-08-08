package org.example.spring.create;

import org.example.spring.Annotation.Lazy;
import org.example.spring.Annotation.Scope;
import org.example.spring.informationEntity.BeanDefinition;

import java.util.List;
import java.util.Map;

import static org.example.spring.SuHanApplication.BEANDEFINITION_MAP;

import static org.example.spring.scan.AnnotationBeanNameGenerator.generateName;

public class CreatBeanDefinitions {

    public static void CreatBeanDefinitionMap(Class<?> clazz, List<Object> generateBeanDefinition) {
        ClassLoader SuhanClassLoader = clazz.getClassLoader();
        for(Object beanDefinition : generateBeanDefinition){
            String beanName = generateName(beanDefinition);
            BEANDEFINITION_MAP.computeIfAbsent(beanName, k -> (BeanDefinition) beanDefinition);
        }

        //接下来配置每个definition
        for (Map.Entry<String,BeanDefinition> entry : BEANDEFINITION_MAP.entrySet()){

            //先获取一个beanDefinition对象
            BeanDefinition bd = entry.getValue();
            try {

                //然后获取一个类的class对象
                Class<?> class2 = SuhanClassLoader.loadClass(bd.getClassName().toString().substring(6));


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

}

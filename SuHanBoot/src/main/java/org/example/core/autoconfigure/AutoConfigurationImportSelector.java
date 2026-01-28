package org.example.core.autoconfigure;

import org.example.core.annotation.Condition;
import org.example.core.annotation.Conditional;
import org.example.core.annotation.ConditionalOnClass;
import org.example.core.beanAware.ApplicationAware;
import org.example.core.context.ApplicationContext;
import org.example.core.informationEntity.*;
import org.example.core.util.AnnotationUtil;
import org.example.core.util.BeanUtil;
import org.example.core.util.SPIUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoConfigurationImportSelector implements ImportSelector, ApplicationAware {

    ClassLoader classLoader;
    ApplicationContext context;

    Map<Annotation ,  Condition> conditionMap = new HashMap<>();

    //任务清单：
    // 1.对ConfigurationClassPostProcessor进行扩展，使其可以接受ImportSelector接口的多个返回 √
    // 2.对AutoConfigurationImportSelector进行编写，使其可以读取spring.factroies文件中的自动配置类
    // 2.1 此处有拓展想法，我只编写对自动配置类读取的方法，然后写成策略模式自己去扩充读什么
    // 3.对@Conditional注解的相关判断逻辑进行完善


    @Override
    public void applicationAware(ApplicationContext applicationContext) {
        context = applicationContext;
    }


    @Override
    public List<String> selectImports(AnnotationMetadata annotationMetadata) {
        List<String> autoConfiglist = new ArrayList<>();
        classLoader = Thread.currentThread().getContextClassLoader();
        try {
            //让我理下这里的流程：
            //1.首先获取配置类，对配置类进行递归拿注解，看有没有 Contional 注解
            //2.有就去创造注解和判断器的对应关系，然后进行逐个判断
            //3.如果最后都是通过的，该类加到结果中
            //4.然后在第三条满足的前提下，继续对方法进行判断 （此处错误，方法交给@Bean去处理，第五条无视）
            //5.对方法进行判断通过，直接将其加入结果中

            List<String> autoConfig = SPIUtil.getSPIValue("org.springframework.boot.autoconfigure.EnableAutoConfiguration");
            for (String name : autoConfig) {
                Class<?> clazz = classLoader.loadClass(name);
                List<Annotation> annotationsList = AnnotationUtil.getAnnotationsList(clazz, new ArrayList<>());
                if(isMatch(new DefaultAnnotatedTypeMetadata(clazz),annotationsList)){
                    autoConfiglist.add(name);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return autoConfiglist;
    }
    private boolean isMatch(AnnotatedTypeMetadata annotationMetadata, List<Annotation> annotationsList) throws Exception {
        List<Conditional> targetAnnotations = AnnotationUtil.getTargetAnnotations(annotationsList, Conditional.class);
        boolean isMatch = true;
        for(Conditional conditional:targetAnnotations){
            //把判断器取出来
            Class<?> conditionClass = conditional.value()[0];
            //实例然后进行调用
            Condition bean = (Condition) BeanUtil.instantiateClass(conditionClass);
            BeanUtil.applyAware(bean);
            if(!bean.matches(context,annotationMetadata)){
                isMatch = false;
                break;
            }
        }
        return isMatch;
    }
}

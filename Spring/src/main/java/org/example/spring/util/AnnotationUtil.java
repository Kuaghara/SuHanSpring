package org.example.spring.util;

import org.example.spring.beanFactoryPostProcessor.ConfigurationClassParser;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.reader.AnnotationBeanDefinitionReader;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {

    //确认List中有无该注解的实例
    public static Boolean listIncludeAnnotation(List<Annotation> annotations , Class<? extends Annotation> annotation){
        for(Annotation ann : annotations){
            if(annotation.isInstance( ann)){
                return true;
            }
        }
        return false;
    }

    public static void ChangeLocation(List<BeanDefinition> bdList,int i ,int j){
        BeanDefinition temp = bdList.get(i);
        bdList.set(i, bdList.get( j));
        bdList.set(j, temp);
    }

    public static List<Annotation> getAnnonationsList(BeanDefinition bd , BeanDefinitionRegistry bdr){
        ConfigurationClassParser parser = new ConfigurationClassParser(new AnnotationBeanDefinitionReader(bdr));
        List<Annotation> ann = new ArrayList<>();
        if(bd.getAllAnnotation().isEmpty()){
            ann = parser.parseAnnotation(bd.getClazz(),new ArrayList<>());
            bd.addAllAnnotation(ann);
        }
        else  ann = bd.getAllAnnotation();
        return ann;
    }
}

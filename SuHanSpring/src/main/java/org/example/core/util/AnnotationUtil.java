package org.example.core.util;

import org.example.core.annotation.Configuration;
import org.example.core.annotation.Order;
import org.example.core.beanFactoryPostProcessor.ConfigurationClassParser;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.context.reader.AnnotationBeanDefinitionReader;
import org.example.core.informationEntity.BeanDefinition;

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
        List<Annotation> ann;
        if(bd.getAllAnnotation().isEmpty()){
            ann = parser.parseAnnotation(bd.getClazz(),new ArrayList<>());
            bd.addAllAnnotation(ann);
        }
        else  ann = bd.getAllAnnotation();
        return ann;
    }
    public static  Boolean isConfigurationClass(Class<?> clazz){
        return clazz.isAnnotationPresent(Configuration.class);
    }
    public static void beanDefinitionSort(List<BeanDefinition> beanDefinitionList , BeanDefinitionRegistry bdr) {
        for(int i = 0; i < beanDefinitionList.size(); i++){
            for(int j = i + 1; j < beanDefinitionList.size(); j++){
                BeanDefinition bd1 = beanDefinitionList.get(i);
                BeanDefinition bd2 = beanDefinitionList.get(j);
                List<Annotation> ann1 = AnnotationUtil.getAnnonationsList(bd1,bdr);
                List<Annotation> ann2 = AnnotationUtil.getAnnonationsList(bd2,bdr);
                if(AnnotationUtil.listIncludeAnnotation(ann1, Order.class)){
                    if(AnnotationUtil.listIncludeAnnotation(ann2, Order.class)){
                        if(getOrderCount(ann1) < getOrderCount(ann2)){
                            AnnotationUtil.ChangeLocation(beanDefinitionList, i, j);
                        }
                    }
                }
                else {
                    if (AnnotationUtil.listIncludeAnnotation(ann2, Order.class)){
                        AnnotationUtil.ChangeLocation(beanDefinitionList, i, j);
                    }
                }
            }
        }
    }
    private static int getOrderCount(List<Annotation> a1){
        for(Annotation ann : a1){
            if(ann instanceof Order){
                return ((Order) ann).value();
            }
        }
        return 0;
    }
}


package org.example.spring.util;

import org.example.spring.informationEntity.BeanDefinition;

import java.lang.annotation.Annotation;
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

}

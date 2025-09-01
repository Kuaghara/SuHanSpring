package org.example.spring.scan;

import org.example.spring.informationEntity.BeanDefinition;

public class BeanNameGenerator {
    public static String generateName(Object beanDefinition){

        String name = ((BeanDefinition) beanDefinition).getClassName().toString();

        //简简单单的截取下类名
        int index=0;
        for (int i=0 ; i < name.length() ; i++){
            if(name.charAt(i) == '.'){
                index = i;
            }
        }
        name = name.substring(index+1);


        return name;
    }
    public static String generateName(String name){

        int index=0;
        for (int i=0 ; i < name.length() ; i++){
            if(name.charAt(i) == '.'){
                index = i;
            }
        }
        name = name.substring(index+1);


        return name;
    }
}

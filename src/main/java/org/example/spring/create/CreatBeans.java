package org.example.spring.create;

import org.example.spring.Annotation.Autowired;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.InstantiationAwareBeanPostProcessor;
import org.example.spring.beanPostProcessor.MergedBeanDefinitionPostProcessor;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.example.spring.SuHanApplication.SINGLETONBEAN_MAP;
import static org.example.spring.SuHanApplication.SUHANCLASSLOADER;
import static org.example.spring.create.Creates.*;
import static org.example.spring.create.InjectingBeans.injectingBean;

public class CreatBeans {
    public static void creatSingletonBean(String beanName,BeanDefinition beanDefinition) {

            if(beanDefinition.getScope().equals("prototype")|beanDefinition.getLazy().equals("true")){
                return;
            }
            else{
                SINGLETONBEAN_MAP.put(beanName,creatBean(beanDefinition));
            }


    }
    public static Object creatBean(BeanDefinition bd){

        try {
            String className = bd.getClazz().getName();
            Constructor<?>[] constructors = SUHANCLASSLOADER.loadClass(className).getConstructors();
            Constructor<?> theConstructor = null;

            //先判断@Autowired注解数量及其报错情况
            int i = 0,j = 0;
            for (Constructor<?> constructor : constructors){
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    i++;
                    Autowired declaredAnnotation = constructor.getDeclaredAnnotation(Autowired.class);
                    if(declaredAnnotation.required()){
                        j++;
                    }
                }
            }
            if (i!=0) {
                if(j == i || ( j == 1 && i >= 2*j ) ){
                    throw new RuntimeException("过多被Autowired注解的构造方法");
                }
            }

            //获取构造方法,没有自定义就给无参，有就给自定义的
            if(constructors.length == 1){
                theConstructor = constructors[0];
            } else if (constructors.length == 2) {
                theConstructor = constructors[1];
            }
            else{
                //这里我是真不知道怎么办了，只能返回一个无参的了
                theConstructor = constructors[0];
            }
            Object bean = theConstructor.newInstance();
            if(bd.getScope().equals("prototype")){
                Method[] methods = SUHANCLASSLOADER.loadClass(className).getDeclaredMethods();
                int k = 0;
                for(Method method : methods){
                    if(method.getName().equals("postProcessBeforeInstantiation")){
                        bean = method.invoke(bean,bd.getClazz(),bd.getClassName());
                        k++;
                        break;
                    }
                }
                if(k != 1){
                    String beanName = bd.getClassName().toString();
                    //实例化
                    creatSingletonBean(beanName, bd);

                    //实例化后
                    if (isRealize(bd, InstantiationAwareBeanPostProcessor.class)) {
                        try {
                            ((InstantiationAwareBeanPostProcessor)bean).postProcessAfterInstantiation(bean, beanName);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    //实例化后的对beandefinition的操作
                    if (isRealize(bd, MergedBeanDefinitionPostProcessor.class)) {
                        applyPostProcessMergedBeanDefinition(bd , bd.getClazz() , beanName);
                    }

                    //依赖注入
                    injectingBean(beanName);

                    //初始化前
                    bean = SINGLETONBEAN_MAP.get(beanName);
                    if (isRealize(bd, BeanPostProcessor.class)) {
                        Object processedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
                        if (processedBean != null) {
                            bean = processedBean;
                            SINGLETONBEAN_MAP.put(beanName, bean);
                        }
                    }

                    //初始化后
                    bean = SINGLETONBEAN_MAP.get(beanName);
                    if(isRealize(bd, BeanPostProcessor.class)) {
                        Object processedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                        if (processedBean != null) {
                            SINGLETONBEAN_MAP.put(beanName, processedBean);
                        }
                    }
                }
            }


            return bean;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }




    }

}

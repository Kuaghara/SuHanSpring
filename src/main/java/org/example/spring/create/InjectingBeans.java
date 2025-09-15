package org.example.spring.create;

import net.sf.cglib.proxy.Factory;
import org.example.spring.beanPostProcessor.BeanPostProcessor;
import org.example.spring.beanPostProcessor.ProxyBeanPostProcessor;
import org.example.spring.informationEntity.AutoElement;
import org.example.spring.informationEntity.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.singletonObjects;

//@Deprecated
//public class InjectingBeans {
//    //存储未查找到依赖注入对象的实例
//    private static Map< Object , Class<?> > creatingObject = new HashMap<>();
//
//    public static void injectingBean(BeanDefinition bd) {
//        //此处的bd为当前正在注入的beanDefinition
//        String name = bd.getClassName().toString();
//        List<AutoElement> autoElements = .get(name);
//        if (autoElements != null) {
//            for (AutoElement autoElement : autoElements) {
//                injectField(bd,autoElement);
//            }
//        }
//    }
//
//     private static void injectField(BeanDefinition bd,AutoElement autoElement) {
//        Field field = autoElement.getField();
//        field.setAccessible(true);
//        String fieldClassName = field.getType().getSimpleName();
//        BeanPostProcessor postProcessor = new ProxyBeanPostProcessor();
//         Object targetBean =getEarlyBean(bd.getClassName().toString());//获取当前正在注入的bean
//         Object bean = getEarlyBean(fieldClassName);//获取被注入对象
//
//        //从BEANDEFINITION_MAP中查找注入对象是否存在
//         //此处为注入对象的beanDefinition
//         BeanDefinition beanDefinition = BEANDEFINITION_MAP.get(fieldClassName);
//        if(beanDefinition != null){
//            //判断被注入对象是不是单例
//
//            if(bean != null){
//                try {
//                    field.set(targetBean, bean);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            //多态找得到就找，找不到反射创建一个然后aop进去不管了
//            else if (bd.getScope().equals("prototype")) {
//                Object prototypeBean = singletonObjects.get(fieldClassName);
//                if (prototypeBean != null) {
//                    try {
//                        field.set(targetBean, prototypeBean);
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                else {
//                    try {
//                        prototypeBean = beanDefinition.getClazz().getConstructor().newInstance();
//                        field.set(field.getDeclaringClass(),
//                                postProcessor.postProcessAfterInitialization(prototypeBean, fieldClassName));
//                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
//                             IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                }
//            }
//
//            //此处进入循环依赖部分
//            //没有获取到对象，此时先对依赖注入进行延后到getBean时实现，之后判断是否为依赖循环
//            //此处判断为查看源码后对ai进行提问，我想出的解决办法
//            else{
//                creatingObject.put(targetBean,beanDefinition.getClazz());
//                ObjectFactory<Object> factory = new ObjectFactory<>() {
//                    @Override
//                    public Object getObject() throws Exception {
//                        //我应该获取一个需要被注入的实例，无需依赖注入，并且如果有aop我应该去实现aop。。。
//                        Object bean = creatBean(beanDefinition);
//                        return postProcessor.postProcessAfterInitialization(bean,fieldClassName);
//                    }
//                };
//                CircularDependency.addFactory(fieldClassName,factory);
//                //需要在此处完成ObjectFactory的实现，并且将对象添加到singletonFactories中
//            }
//        }
//    }
//
//
//}



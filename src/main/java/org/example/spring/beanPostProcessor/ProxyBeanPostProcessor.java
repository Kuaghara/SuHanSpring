package org.example.spring.beanPostProcessor;

import org.example.spring.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.proxy.context.*;



import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.AOP_LIST;
import static org.example.spring.context.BeanFactory.DefaultListableBeanFactory.beanDefinitionMap;

public class ProxyBeanPostProcessor implements  SmartInstantiationAwareBeanPostProcessor {

    //两个操作，1判断是否为AOP并且位于BEANDEFINITION中 2根据该AOP的请款去选择使用哪种代理方法进行代理
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(AnnotationResolver.classFilter(bean.getClass())){
            if(bean.getClass().isInterface()){
                return new JdkProxyFactory(bean).getProxy();
            }
            return new CglibProxyFactory(bean).getProxy();
        }
        return bean;
    }
    public static void registerAdvisor(BeanDefinition bd){
        if(AOP_LIST.contains(bd.getClazz())){
            //扔到Adviosr表中，等待后续动作
            AnnotationResolver annotationResolver = new AnnotationResolver();
            annotationResolver.parse(bd.getClazz());
        }
    }

    //前期扫描@Aspect注解的
    @Override
    public void applyBeforeInstantiationMethod() {
        for(BeanDefinition bd : beanDefinitionMap.values()){ //这个也是，同上
            ProxyBeanPostProcessor.registerAdvisor(bd);
        }
    }
    public BeanDefinition addBeanPostProcessor() {
        AnnotatedGenericBeanDefinition bd = new AnnotatedGenericBeanDefinition();
        bd.setClassName(ProxyBeanPostProcessor.class.getSimpleName());
        bd.setClazz(ProxyBeanPostProcessor.class);
        bd.setScope("singleton");
        return bd;
    }
}

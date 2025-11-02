package org.example.spring.proxy;

import org.example.spring.beanPostProcessor.MergedBeanDefinitionPostProcessor;
import org.example.spring.beanPostProcessor.SmartInitializationAwareBeanPostProcessor;
import org.example.spring.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.spring.informationEntity.BeanDefinition;
import org.example.spring.proxy.annotation.Aspect;
import org.example.spring.proxy.context.AnnotationResolver;
import org.example.spring.proxy.context.CglibProxyFactory;
import org.example.spring.proxy.context.JdkProxyFactory;


public class ProxyBeanPostProcessor implements MergedBeanDefinitionPostProcessor, SmartInitializationAwareBeanPostProcessor {

    //两个操作，1判断是否为AOP并且位于beanDefinition_Map中 2根据该AOP的请款去选择使用哪种代理方法进行代理
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (AnnotationResolver.classFilter(bean.getClass())) {
            if (bean.getClass().isInterface()) {
                return new JdkProxyFactory(bean).getProxy();
            }
            return new CglibProxyFactory(bean).getProxy();
        }
        return bean;
    }

    /// 此处的设想是用于提前aop，实际上就是重新调用了一次初始化后
    @Override
    public Object applyAfterInitializationMethod(String name, Object bean) {
       return postProcessAfterInitialization(bean, name);
    }

    public static void registerAdvisor(BeanDefinition bd) {
        if (bd.getClazz().isAnnotationPresent(Aspect.class)) {
            //扔到Adviosr表中，等待后续动作
            AnnotationResolver annotationResolver = new AnnotationResolver();
            annotationResolver.parse(bd.getClazz());
        }
    }

    @Override
    public void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, Class<?> beanType, String beanName) {
       registerAdvisor(beanDefinition);
    }

    @Deprecated
    public static BeanDefinition asBeanDefinitionAdd() {
        AnnotatedGenericBeanDefinition bd = new AnnotatedGenericBeanDefinition();
        bd.setClassName(ProxyBeanPostProcessor.class.getSimpleName());
        bd.setClazz(ProxyBeanPostProcessor.class);
        bd.setScope("singleton");
        return bd;
    }
}

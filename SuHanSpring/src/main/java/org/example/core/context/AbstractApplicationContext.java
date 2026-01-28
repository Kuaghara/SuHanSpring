package org.example.core.context;

import org.example.core.beanFactoryPostProcessor.BeanFactoryPostProcessor;
import org.example.core.beanFactoryPostProcessor.ConfigurationClassPostProcessor;
import org.example.core.beanFactoryPostProcessor.PostProcessorRegistrationDelegate;
import org.example.core.beanPostProcessor.AutowiredAnnotationBeanProcessor;
import org.example.core.context.beanFactory.BeanFactory;
import org.example.core.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.core.context.beanFactory.DefaultListableBeanFactory;
import org.example.core.context.event.*;
import org.example.core.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.core.util.BeanUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    BeanFactory parentBeanFactory;
    boolean circularDependencies = true;
    ApplicationEventMulticaster applicationEventMulticaster = new DefaultApplicationEventMulticaster();

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    public void refresh() {
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        //注册一些基本的beanFactoryPostProcessor
        registrationBasicsBeanFactoryPostProcessors(beanFactory);
        //运行beanFactoryPostProcessor
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory);
        //创建仍然未完成创建的bean（内部有beanPostProcessor的运行）
        finishPreInstantiateSingletons(beanFactory);


    }

    public abstract DefaultListableBeanFactory getBeanFactory() throws IllegalStateException;


    private void registrationBasicsBeanFactoryPostProcessors(DefaultListableBeanFactory registry) {
        //拿来扫描bd和解析配置类的
        if (!registry.containsBeanDefinition("ConfigurationClassPostProcessor")) {
            registry.registerBeanDefinition("ConfigurationClassPostProcessor", new AnnotatedGenericBeanDefinition(ConfigurationClassPostProcessor.class));
            try {
                BeanFactoryPostProcessor configurationClassPostProcessor = (BeanFactoryPostProcessor) this.getBean("ConfigurationClassPostProcessor");
                registry.addBeanFactoryPostProcessor(configurationClassPostProcessor);
                registry.registerSingleton("ConfigurationClassPostProcessor", configurationClassPostProcessor);
                BeanUtil.setBeanFactory( registry);
            } catch (Exception e) {
                throw new RuntimeException("创建基础的ConfigurationClassPostProcessor时报错");
            }
        }

        //进行自动注入扫描的
        /// 在PostProcessorRegistrationDelegate中存在扫描然后注册所有beanPostProcessor的逻辑，这里只需要添加bd即可
        if(!registry.containsBeanDefinition("AutowiredAnnotationBeanProcessor")){
            registry.registerBeanDefinition("AutowiredAnnotationBeanProcessor", new AnnotatedGenericBeanDefinition(AutowiredAnnotationBeanProcessor.class));
        }

        applicationEventMulticaster.addApplicationListener(new DestoryApplicationListener());
    }

    private void finishPreInstantiateSingletons(ConfigurableListableBeanFactory factory) {
        factory.cyclicDependentState(circularDependencies);
        factory.preInstantiateSingletons();
    }

    @Override
    public void disableCircularDependencies() {
        circularDependencies = false;
    }

    @Override
    public String getApplicationName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Object getBean(String beanName) {
        try {
            return getBeanFactory().getBean(beanName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) throws Exception {
        return getBeanFactory().getBean(beanName, clazz);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    @Override
    public Boolean containsBean(String beanName) {
        return getBeanFactory().containsBean(beanName);
    }

    @Override
    public Boolean isTypeMatch(String name, Class<?> clazz) {
        return getBeanFactory().isTypeMatch(name, clazz);
    }

    @Override
    public List<String> getBeanNameForType(Class<?> clazz) {
        return getBeanFactory().getBeanNameForType(clazz);
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Override
    public void setParentBeanFactory(BeanFactory beanFactory) {
        this.parentBeanFactory = beanFactory;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return getBeanFactory().containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }


    @Override
    public void publishEvent(ApplicationEvent<?> applicationEvent) {
        applicationEventMulticaster.multicastEvent(applicationEvent);
    }

    @Override
    public void close() {
            publishEvent(new DestoryApplicationEvent(this));
    }
}

package org.example.spring.context;

import org.example.spring.beanFactoryPostProcessor.BeanFactoryPostProcessor;
import org.example.spring.beanFactoryPostProcessor.ConfigurationClassPostProcessor;
import org.example.spring.beanFactoryPostProcessor.PostProcessorRegistrationDelegate;
import org.example.spring.beanPostProcessor.AutowiredAnnotationBeanProcessor;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.BeanFactory;
import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.spring.context.beanFactory.DefaultListableBeanFactory;
import org.example.spring.informationEntity.AnnotatedGenericBeanDefinition;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    BeanFactory parentBeanFactory;
    boolean circularDependencies = true;

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
        //我的beanDefinition_map中存储的bean名称为全小写（
        //拿来扫描bd和解析配置类的
        if (!registry.containsBeanDefinition("ConfigurationClassPostProcessor")) {
            registry.registerBeanDefinition("ConfigurationClassPostProcessor", new AnnotatedGenericBeanDefinition(ConfigurationClassPostProcessor.class));
            try {
                BeanFactoryPostProcessor configurationClassPostProcessor = (BeanFactoryPostProcessor) this.getBean("ConfigurationClassPostProcessor");
                registry.addBeanFactoryPostProcessor(configurationClassPostProcessor);
                registry.registerSingleton("ConfigurationClassPostProcessor", configurationClassPostProcessor);
            } catch (Exception e) {
                throw new RuntimeException("创建基础的ConfigurationClassPostProcessor时报错");
            }
        }

        //进行自动注入扫描的
        /// 在PostProcessorRegistrationDelegate中存在扫描然后注册所有beanPostProcessor的逻辑，这里只需要添加bd即可
        if(!registry.containsBeanDefinition("AutowiredAnnotationBeanProcessor")){
            registry.registerBeanDefinition("AutowiredAnnotationBeanProcessor", new AnnotatedGenericBeanDefinition(AutowiredAnnotationBeanProcessor.class));
        }
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
}

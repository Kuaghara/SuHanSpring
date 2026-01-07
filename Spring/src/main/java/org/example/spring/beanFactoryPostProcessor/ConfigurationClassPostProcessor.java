package org.example.spring.beanFactoryPostProcessor;

import org.example.spring.annotation.Configuration;
import org.example.spring.annotation.Order;
import org.example.spring.beanPostProcessor.BeanDefinitionRegistryPostProcessor;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.spring.context.reader.AnnotationBeanDefinitionReader;
import org.example.spring.informationEntity.BeanDefinition;

import java.util.List;


//配置类处理器
//在设想中，该类会是beanDefinition扫描的入口（调用跳转到AnnotationBeanDefinitionReader中）
//以及会完成对配置类的解析(其实对beanDefinition的扫描就是对于@ComponentScan注解的解析)
//解析会跳转到ConfigurationClassParser中
@Order(9)
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor,PriorityOrdered {

    @Override
    public int getOrder() {
        return 9;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        List<String> beanNames = registry.getBeanDefinitionNames();
        for(String beanName : beanNames){
            BeanDefinition bd = registry.getBeanDefinition(beanName);
            if(isConfigurationClass(bd.getClazz())){
                new ConfigurationClassParser(new AnnotationBeanDefinitionReader( registry)).parse(bd , registry);
            }
        }

    }

    private  Boolean isConfigurationClass(Class<?> clazz){
        return clazz.isAnnotationPresent(Configuration.class);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {}


}

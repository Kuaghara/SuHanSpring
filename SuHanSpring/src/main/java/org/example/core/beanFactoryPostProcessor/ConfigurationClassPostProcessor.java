package org.example.core.beanFactoryPostProcessor;

import org.example.core.annotation.Configuration;
import org.example.core.annotation.Order;
import org.example.core.beanPostProcessor.BeanDefinitionRegistryPostProcessor;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.core.context.reader.AnnotationBeanDefinitionReader;
import org.example.core.informationEntity.BeanDefinition;
import org.example.core.util.AnnotationUtil;

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
            if(AnnotationUtil.isConfigurationClass(bd.getClazz())){
                new ConfigurationClassParser(new AnnotationBeanDefinitionReader( registry)).parse(bd , registry);
            }
        }

    }



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {}


}

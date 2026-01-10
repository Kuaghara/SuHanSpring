package org.example.core.annotationHandler;

import org.example.core.beanFactoryPostProcessor.Ordered;
import org.example.core.beanPostProcessor.BeanDefinitionRegistryPostProcessor;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.core.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.core.informationEntity.BeanDefinition;
import org.example.core.util.AnnotationUtil;

import java.util.List;

public class MapperScanRegister implements BeanDefinitionRegistryPostProcessor, Ordered {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("MapperScanPostProcessor")) {
            registry.registerBeanDefinition("MapperScanPostProcessor", new AnnotatedGenericBeanDefinition(MapperScanPostProcessor.class));
        }

        // Register mapper interfaces as bean definitions based on @MapperScan on configuration classes.
        List<String> beanNames = registry.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition bd = registry.getBeanDefinition(beanName);
            Class<?> bdClazz = bd.getClazz();
            if (bdClazz != null && AnnotationUtil.isConfigurationClass(bdClazz)) {
                new MapperScanHandler().parseMapperScan(bdClazz, registry, bd);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

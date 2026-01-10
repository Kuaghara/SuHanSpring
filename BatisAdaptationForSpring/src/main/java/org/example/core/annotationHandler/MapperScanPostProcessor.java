package org.example.core.annotationHandler;

import org.example.core.annotation.Autowired;
import org.example.core.annotation.Order;
import org.example.core.beanPostProcessor.InstantiationAwareBeanPostProcessor;
import org.example.core.sqlSessionTemplate.SqlSessionTemplate;

/**
 * Only responsible for creating Mapper proxies.
 * Mapper interface bean definitions are registered by {@link MapperScanRegister}.
 */
@Order(1)
public class MapperScanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        if (beanClass.isInterface()) {
            return sqlSessionTemplate.getMapper(beanClass);
        }
        return null;
    }
}

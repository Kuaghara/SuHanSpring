package org.example.spring.beanPostProcessor;

import org.example.spring.informationEntity.BeanDefinition;

@Deprecated
/// 孩子不懂事瞎写着玩的
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    //此接口设计给@Autowired注解和@Aspect注解扫描使用
    default void applyBeforeInstantiationMethod() {
    }

    ;

    default void applyAfterInstantiationMethod(BeanDefinition bd) throws Exception {
    }

    ;
}

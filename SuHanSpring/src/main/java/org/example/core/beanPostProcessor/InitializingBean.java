package org.example.core.beanPostProcessor;

public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}

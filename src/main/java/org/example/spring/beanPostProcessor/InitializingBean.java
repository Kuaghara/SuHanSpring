package org.example.spring.beanPostProcessor;

public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}

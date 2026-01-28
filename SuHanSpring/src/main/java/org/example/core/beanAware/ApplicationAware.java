package org.example.core.beanAware;

import org.example.core.context.ApplicationContext;

public interface ApplicationAware extends BeanAware{
    default void applicationAware(ApplicationContext applicationContext) {
    }
}

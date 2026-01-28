package org.example.core.context.beanFactory;

import org.example.core.context.ApplicationContext;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory, ConfigurableBeanFactory {
    void cyclicDependentState(boolean state);
    ApplicationContext getApplicationContext();
    void setApplicationContext(ApplicationContext applicationContext);
}

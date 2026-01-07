package org.example.spring.context.beanFactory;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory, ConfigurableBeanFactory {
    void cyclicDependentState(boolean  state);
}

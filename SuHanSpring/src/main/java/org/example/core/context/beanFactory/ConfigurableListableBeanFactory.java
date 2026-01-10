package org.example.core.context.beanFactory;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory, ConfigurableBeanFactory {
    void cyclicDependentState(boolean  state);
}

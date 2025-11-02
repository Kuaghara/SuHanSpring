package org.example.spring.context;

import org.example.spring.context.beanFactory.HierarchicalBeanFactory;
import org.example.spring.context.beanFactory.ListableBeanFactory;

public interface ApplicationContext extends ListableBeanFactory , HierarchicalBeanFactory  {
    String getApplicationName();
    void disableCircularDependencies();


}

package org.example.spring.context;

import org.example.spring.context.beanFactory.HierarchicalBeanFactory;
import org.example.spring.context.beanFactory.ListableBeanFactory;
import org.example.spring.context.event.ApplicationEventPublisher;

public interface ApplicationContext extends ListableBeanFactory , HierarchicalBeanFactory , ApplicationEventPublisher {
    String getApplicationName();
    void disableCircularDependencies();
}

package org.example.core.context;

import org.example.core.context.beanFactory.HierarchicalBeanFactory;
import org.example.core.context.beanFactory.ListableBeanFactory;
import org.example.core.context.event.ApplicationEventPublisher;

public interface ApplicationContext extends ListableBeanFactory , HierarchicalBeanFactory , ApplicationEventPublisher {
    String getApplicationName();
    void disableCircularDependencies();
}

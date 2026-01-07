package org.example.spring.context.event;

import org.example.spring.context.ApplicationContext;

public interface ApplicationEventMulticaster {
    void addApplicationListener(ApplicationListener<?> listener);
    void removeApplicationListener(ApplicationListener<?> listener);
    void multicastEvent(ApplicationEvent<?> event);
}

package org.example.spring.context.event;

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent<?>> {
    void onEvent(ApplicationEvent<?> applicationEvent);
}

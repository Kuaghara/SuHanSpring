package org.example.core.context.event;

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent<?>> {
    void onEvent(ApplicationEvent<?> applicationEvent);
}

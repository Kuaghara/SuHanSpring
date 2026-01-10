package org.example.core.context.event;

@FunctionalInterface
public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent<?> applicationEvent);
}

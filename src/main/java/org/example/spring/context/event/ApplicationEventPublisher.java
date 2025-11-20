package org.example.spring.context.event;

@FunctionalInterface
public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent<?> applicationEvent);
}

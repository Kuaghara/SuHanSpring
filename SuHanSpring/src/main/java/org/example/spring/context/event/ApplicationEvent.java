package org.example.spring.context.event;

import java.time.Instant;
import java.util.EventObject;

public class ApplicationEvent<T> extends EventObject {

    private final Long timestamp = Instant.now().getEpochSecond();

    public ApplicationEvent(T source) {
        super(source);
    }

    public Long getTimestamp() {
        return timestamp;
    }
}

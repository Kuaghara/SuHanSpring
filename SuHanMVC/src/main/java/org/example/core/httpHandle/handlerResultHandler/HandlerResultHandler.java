package org.example.core.httpHandle.handlerResultHandler;

import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Method;

public interface HandlerResultHandler {
    void handle(HttpExchange exchange, Object invoked);

    boolean isMatch(Object controller, Method method);
}

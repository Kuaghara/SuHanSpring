package org.example.core.httpHandle.handlerResultHandler;

import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface HandlerResultHandler {
    void handle(HttpExchange exchange ,Object invoked);

    boolean isMatch( Object controller, Method method);
}

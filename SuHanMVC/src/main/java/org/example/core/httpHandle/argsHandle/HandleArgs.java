package org.example.core.httpHandle.argsHandle;

import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Parameter;

public interface HandleArgs {

    boolean support(HttpExchange exchange, Parameter parameters);

    Object handle(HttpExchange exchange, Parameter parameter);
}

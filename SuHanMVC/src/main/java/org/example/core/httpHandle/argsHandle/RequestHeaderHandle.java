package org.example.core.httpHandle.argsHandle;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.annotation.RequestHeader;

import java.lang.reflect.Parameter;

public class RequestHeaderHandle implements HandleArgs {
    @Override
    public boolean support(HttpExchange exchange, Parameter parameter) {
        return parameter.isAnnotationPresent(RequestHeader.class);
    }

    @Override
    public Object handle(HttpExchange exchange, Parameter parameter) {
        RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
        String paramName = requestHeader.value();
        String defaultValue = requestHeader.defaultValue();
        boolean required = requestHeader.required();

        String value = exchange.getRequestHeaders().getFirst(paramName);
        if (value == null && required) {
            throw new RuntimeException("参数 " + paramName + " 不能为空");
        }
        if (value == null && !defaultValue.isEmpty()) {
            value = defaultValue;
        }
        return value;
    }

}

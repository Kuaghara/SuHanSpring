package org.example.core.httpHandle.argsHandle;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.annotation.RequestParam;

import java.lang.reflect.Parameter;

public class RequestParamHandle implements HandleArgs {
    @Override
    public boolean support(HttpExchange exchange, Parameter parameters) {
        return parameters.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object handle(HttpExchange exchange, Parameter parameter) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        String paramName = requestParam.value();
        String defaultValue = requestParam.defaultValue();
        boolean required = requestParam.required();

        String path = exchange.getRequestURI().getQuery();
        String value = null;

        if (path != null && !path.isEmpty()) {
            String[] params = path.split("&");
            for (String param : params) {
                String[] keyAndValue = param.split("=");
                if (keyAndValue.length == 2) {
                    if (paramName.equals(keyAndValue[0])) {
                        value = keyAndValue[1];
                    }
                }
            }
        }

        if (value == null && required) {
            throw new RuntimeException("参数 " + paramName + " 不能为空");
        }
        if (!defaultValue.isEmpty() && value == null) {
            value = defaultValue;
        }
        return value;
    }
}

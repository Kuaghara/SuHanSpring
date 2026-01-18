package org.example.core.httpHandle;

import java.lang.reflect.Method;

public class HandlerMethod {
    String controllerName;
    Method method;

    public HandlerMethod(String controllerName, Method method) {
        this.controllerName = controllerName;
        this.method = method;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}

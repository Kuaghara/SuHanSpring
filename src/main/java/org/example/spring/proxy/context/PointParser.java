package org.example.spring.proxy.context;

import java.lang.reflect.Method;

public interface PointParser {
    Advisor getAdvisor(Method method, Object Aspect);
}

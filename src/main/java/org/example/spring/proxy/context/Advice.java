package org.example.spring.proxy.context;

import java.lang.reflect.Method;
import java.util.List;

public interface Advice {
     Object invoke(MethodInvocation invocation) throws Throwable;
}

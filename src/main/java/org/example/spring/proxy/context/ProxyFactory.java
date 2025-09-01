package org.example.spring.proxy.context;

import java.lang.reflect.Method;

public interface ProxyFactory{
    Object getProxy();
    void addAdvisor(Advisor advisor);

}

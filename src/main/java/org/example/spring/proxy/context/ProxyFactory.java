package org.example.spring.proxy.context;

public interface ProxyFactory {
    Object getProxy();

    void addAdvisor(Advisor advisor);

}

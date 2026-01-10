package org.example.core.proxy.context;

public interface ProxyFactory {
    Object getProxy();

    void addAdvisor(Advisor advisor);

}

package org.example.spring.context.event;

import org.example.spring.context.ThreadPoolManager;

public class DestoryApplicationListener implements ApplicationListener<DestoryApplicationEvent>{

    @Override
    public void onEvent(ApplicationEvent<?> applicationEvent) {
        ThreadPoolManager.shutdown();
    }
}

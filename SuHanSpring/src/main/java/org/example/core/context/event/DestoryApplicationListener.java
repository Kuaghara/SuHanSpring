package org.example.core.context.event;

import org.example.core.context.ThreadPoolManager;

public class DestoryApplicationListener implements ApplicationListener<DestoryApplicationEvent>{

    @Override
    public void onEvent(ApplicationEvent<?> applicationEvent) {
        ThreadPoolManager.shutdown();
    }
}

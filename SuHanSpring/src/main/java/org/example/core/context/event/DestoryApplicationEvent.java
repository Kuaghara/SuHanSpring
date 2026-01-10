package org.example.core.context.event;

import org.example.core.context.ApplicationContext;

public class DestoryApplicationEvent extends ApplicationEvent<ApplicationContext>{
    ApplicationContext applicationContext;
    public DestoryApplicationEvent(ApplicationContext source) {
        super(source);
    }
    @Override
    public ApplicationContext getSource() {
        return applicationContext;
    }
}

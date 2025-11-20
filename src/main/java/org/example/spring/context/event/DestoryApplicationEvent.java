package org.example.spring.context.event;

import org.example.spring.context.ApplicationContext;

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

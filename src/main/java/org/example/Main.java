package org.example;

import org.example.entity.UserService;
import org.example.spring.context.AnnotationApplicationContext;
import org.example.spring.context.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        //不许偷懒，尽可能地去完成所有spring的逻辑
        ApplicationContext application = new AnnotationApplicationContext(MainConfig.class);
        UserService user = (UserService) application.getBean("UserService");
        user.test();

    }
}
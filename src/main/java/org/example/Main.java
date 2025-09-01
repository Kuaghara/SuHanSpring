package org.example;

import org.example.entity.UserService;
import org.example.spring.SuHanApplication;


import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args){
        //不许偷懒，尽可能地去完成所有spring的逻辑
        SuHanApplication application = new SuHanApplication(MainConfig.class);
        UserService user = (UserService) application.getBean("UserService");
        user.test();
    }
}
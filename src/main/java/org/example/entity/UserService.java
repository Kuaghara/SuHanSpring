package org.example.entity;


import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;


@Component
public class UserService {
    String name;

    @Autowired
    public OldUserService old;

    public OldUserService getOld() {
        return old;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void test() {
        System.out.println("hello world");
    }


}
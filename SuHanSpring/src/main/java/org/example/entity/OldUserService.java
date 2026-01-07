package org.example.entity;


import org.example.spring.annotation.Component;

@Component
public class OldUserService {
    public OldUserService() {
    }

    public void test() {
        System.out.println("oldUserService");
    }
}
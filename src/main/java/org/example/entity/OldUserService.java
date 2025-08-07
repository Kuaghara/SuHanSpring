package org.example.entity;


import org.example.spring.Annotation.Component;

@Component
public class OldUserService {
    public OldUserService() {
    }

    public void test() {
        System.out.println("oldUserService");
    }
}
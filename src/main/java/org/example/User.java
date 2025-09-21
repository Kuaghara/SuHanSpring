package org.example;

import org.example.entity.CircularDependency_Test;
import org.example.spring.annotation.Autowired;

public class User {
    private String name;
    private int age;

    @Autowired
    private CircularDependency_Test circularDependency_test;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void test() {
        System.out.println("test");
    }
}

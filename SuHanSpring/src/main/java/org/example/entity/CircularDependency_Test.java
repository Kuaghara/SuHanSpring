package org.example.entity;

import org.example.User;
import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;

@Component
public class CircularDependency_Test {
    @Autowired
    private Crt2 crt2;

    public CircularDependency_Test() {
    }


}

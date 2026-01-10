package org.example.entity;

import org.example.core.annotation.Autowired;
import org.example.core.annotation.Component;

@Component
public class CircularDependency_Test {
    @Autowired
    private Crt2 crt2;

    public CircularDependency_Test() {
    }


}

package org.example.entity;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.ComponentScan;

@Component
public class Crt2 {
    @Autowired
    private CircularDependency_Test crt1;
}

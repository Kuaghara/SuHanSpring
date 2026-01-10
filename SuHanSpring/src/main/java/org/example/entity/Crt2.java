package org.example.entity;

import org.example.core.annotation.Autowired;
import org.example.core.annotation.Component;

@Component
public class Crt2 {
    @Autowired
    private CircularDependency_Test crt1;
}

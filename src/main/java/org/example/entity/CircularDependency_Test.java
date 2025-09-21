package org.example.entity;

import org.example.User;
import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;

@Component
public class CircularDependency_Test {
    @Autowired
    private User user;

    public CircularDependency_Test() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

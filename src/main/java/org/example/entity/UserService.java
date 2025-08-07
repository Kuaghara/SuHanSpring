package org.example.entity;


import org.example.spring.Annotation.Autowired;
import org.example.spring.Annotation.Component;
import org.example.spring.Annotation.Scope;

import javax.xml.crypto.Data;

@Component
@Scope("prototype")
public class UserService {
    String name;
    Data data;

    public OldUserService getOld() {
        return old;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Autowired
    public OldUserService old;
    public void test(){
        System.out.println("hello world");
    }
}
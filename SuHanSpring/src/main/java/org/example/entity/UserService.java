package org.example.entity;


import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.proxy.annotation.Async;


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

    @Async
    public void testAsync()  {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("这是异步的方法测试");
    }


}
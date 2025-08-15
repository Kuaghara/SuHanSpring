package org.example.entity;


import org.example.spring.Annotation.Autowired;
import org.example.spring.Annotation.Component;
import org.example.spring.Annotation.Scope;
import org.example.spring.beanPostProcessor.InstantiationAwareBeanPostProcessor;


@Component
@Scope("prototype")
public class UserService implements InstantiationAwareBeanPostProcessor {
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


    public void test(){
        System.out.println("hello world");
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws Exception {
        System.out.println("实例化之前");
        return new UserService();
    }
}
package org.example.spring.create;

@FunctionalInterface
//该接口为学习编写中后期创建，之前并不会oop编写，请见谅
public interface  ObjectFactory<T> {
    T getObject() throws Exception;
}

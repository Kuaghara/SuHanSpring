package org.example.core;

import com.sun.net.httpserver.HttpServer;
import org.example.MainConfig;
import org.example.core.context.AnnotationApplicationContext;
import org.example.core.context.ConfigurableApplicationContext;

public class SpringApplication {
    /// 根据我观察源码后发现，相比原来的SpringApplication，这个只多做了一件值得我干的事
    /// 就是有个加载资源
    /// 然后就被扔给原来的context.refresh()了
    /// 但是加载资源的话，我有编写一个工具类读东西。。。
    public static ConfigurableApplicationContext run(Class<?> clazz, String... args) {
        ConfigurableApplicationContext context = new AnnotationApplicationContext(clazz);
        context.refresh(); // 添加必要的刷新步骤

        HttpServer server = (HttpServer) context.getBean(HttpServer.class);
        server.start();

        return context;
    }
}
package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.config.SpringConfig;
import org.example.core.context.AnnotationApplicationContext;
import org.example.core.context.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationApplicationContext(SpringConfig.class);
        HttpServer server = (HttpServer) context.getBean("HttpServer");
        server.start();

    }
}
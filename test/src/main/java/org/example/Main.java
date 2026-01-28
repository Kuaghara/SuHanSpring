package org.example;

import org.example.config.SpringConfig;
import org.example.core.SpringApplication;
import org.example.core.annotation.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
     SpringApplication.run(Main.class);
    }
}
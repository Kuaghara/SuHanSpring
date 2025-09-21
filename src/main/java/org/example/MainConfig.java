package org.example;

import org.example.spring.annotation.Bean;
import org.example.spring.annotation.ComponentScan;
import org.example.spring.annotation.Configuration;
import org.example.spring.proxy.annotation.EnableAsync;

@EnableAsync
@ComponentScan("org.example.entity")
@Configuration
public class MainConfig {

    @Bean
    public User User() {
        return new User();
    }
}
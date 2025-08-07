package org.example;

import org.example.spring.Annotation.Bean;
import org.example.spring.Annotation.ComponentScan;
import org.example.spring.Annotation.Configuration;

@ComponentScan("org.example.entity")
@Configuration
public class MainConfig {

    @Bean
    public User User(){
        return new User();
    }
}
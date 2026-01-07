package org.example;

import org.example.spring.annotation.Bean;
import org.example.spring.annotation.ComponentScan;
import org.example.spring.annotation.Configuration;
import org.example.spring.proxy.annotation.Async;
import org.example.spring.proxy.annotation.EnableAspectJAutoProxy;
import org.example.spring.proxy.annotation.EnableAsync;

@EnableAsync
@EnableAspectJAutoProxy
@Configuration
@ComponentScan("org.example.entity")
public class MainConfig {
    @Bean
    public User user(){
        return new User();
    }

}

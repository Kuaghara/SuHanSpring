package org.example;

import org.example.core.annotation.Bean;
import org.example.core.annotation.ComponentScan;
import org.example.core.annotation.Configuration;
import org.example.core.proxy.annotation.EnableAspectJAutoProxy;
import org.example.core.proxy.annotation.EnableAsync;

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

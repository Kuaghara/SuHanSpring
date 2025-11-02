package org.example;

import org.example.spring.annotation.ComponentScan;
import org.example.spring.annotation.Configuration;
import org.example.spring.proxy.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@Configuration
@ComponentScan("org.example.entity")
public class MainConfig {
}

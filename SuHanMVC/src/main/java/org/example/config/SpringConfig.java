package org.example.config;

import org.example.core.annotation.ComponentScan;
import org.example.core.annotation.Configuration;
import org.example.core.annotation.EnableWebMvc;
import org.example.core.configuartion.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan("org.example.controller")
public class SpringConfig implements WebMvcConfigurer {
}

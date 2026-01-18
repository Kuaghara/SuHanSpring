package org.example.config;

import com.sun.net.httpserver.HttpServer;
import org.example.core.annotation.EnableWebMvc;
import org.example.core.annotation.Bean;
import org.example.core.annotation.ComponentScan;
import org.example.core.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;

@Configuration
@EnableWebMvc
@ComponentScan("org.example.controller")
public class SpringConfig {
}

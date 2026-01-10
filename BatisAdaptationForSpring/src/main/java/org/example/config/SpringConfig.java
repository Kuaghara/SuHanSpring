package org.example.config;

import org.example.core.annotation.Bean;
import org.example.core.annotation.ComponentScan;
import org.example.core.annotation.Configuration;
import org.example.core.annotation.MapperScan;
import org.example.core.sqlSessionTemplate.SqlSessionTemplate;
import org.example.core.sqlsession.SqlSessionFactory;
import org.example.core.sqlsession.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
@ComponentScan("org.example.entity")
@MapperScan("org.example.mapper")
public class SpringConfig {

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws FileNotFoundException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("BatisAdaptationForSpring/src/main/java/org/example/config/mybatis-config.xml"));
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

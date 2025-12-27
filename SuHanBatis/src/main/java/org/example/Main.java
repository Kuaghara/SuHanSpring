package org.example;


import org.example.core.confguration.BaseBuilder;
import org.example.core.confguration.XmlConfigBuilder;
import org.example.core.sqlSession.SqlSession;
import org.example.core.sqlSession.SqlSessionFactory;
import org.example.core.sqlSession.SqlSessionFactoryBuilder;
import org.example.entity.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("SuHanBatis/src/main/resources/mybatis-config.xml"));

        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)){
            User user = new User();
            user.setId(10);
            user.setUsername("SuHan");
            user.setPassword("123456");
            int testMap = sqlSession.insert("testMap", user);
            System.out.println(testMap);
        }
    }

//    public static SqlSessionFactory getSqlSessionFactory() {
//        Configuration config = new Configuration();
//        PooledDataSource dataSource = new PooledDataSource(
//                "com.mysql.cj.jdbd.Driver",
//                "jdbc:mysql://localhost:3306/userdb",
//                "root",
//                "123456"
//        );
//        Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);
//        config.setEnvironment(environment);
//        config.getTypeAliasRegistry().registerAliases("org.example.entity");
//        config.addMappers("org.example.mapper");
//        return new SqlSessionFactoryBuilder().build(config);
//    }
}
package org.example;


import org.example.core.sqlsession.SqlSession;
import org.example.core.sqlsession.SqlSessionFactory;
import org.example.core.sqlsession.SqlSessionFactoryBuilder;
import org.example.entity.TestMapper;
import org.example.entity.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("SuHanBatis/src/main/resources/mybatis-config.xml"));

        try(SqlSession sqlSession = sqlSessionFactory.openSession(false)){
            TestMapper mapper = sqlSession.getMapper(TestMapper.class);
            User user = mapper.getUserById(1);
            System.out.println(user);

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
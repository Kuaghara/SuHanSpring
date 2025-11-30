package org.example;


public class Main {
    public static void main(String[] args) {

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
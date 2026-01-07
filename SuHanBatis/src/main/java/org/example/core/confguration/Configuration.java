package org.example.core.confguration;

import org.example.core.mapper.Mapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private Environment environment;
    private PooledDataSource pooledDataSource;
    private AliasRegistry aliasRegistry = null ;

    List<String> mapperPathList = new ArrayList<>();
    List<Mapper> mapperList = new ArrayList<>();

    public Configuration() {
        this(new Environment(), new PooledDataSource(), new AliasRegistry());
    }

    public Configuration(Environment environment) {
        this(environment, new PooledDataSource(), new AliasRegistry());
    }

    public Configuration(Environment environment, PooledDataSource pooledDataSource){
        this(environment, pooledDataSource, new AliasRegistry());
    }

    public Configuration(Environment environment, PooledDataSource pooledDataSource, AliasRegistry aliasRegistry){
        this.environment = environment;
        this.pooledDataSource = pooledDataSource;
        this.aliasRegistry = aliasRegistry;
    }

    public void setEnvironment(Environment environment){
        this.environment = environment;
        this.pooledDataSource = (PooledDataSource) environment.getDataSource();
    }

    public void addMappers(String packagePath){
        mapperPathList.add(packagePath);
    }
    public void addMapper(Mapper mapper){
        mapperList.add(mapper);
    }
    public List<Mapper> getMapperList(){
        return mapperList;
    }
    public AliasRegistry getTypeAliasRegistry(){
        return aliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public PooledDataSource getPooledDataSource() {
        return pooledDataSource;
    }

    public void setPooledDataSource(PooledDataSource pooledDataSource) {
        this.pooledDataSource = pooledDataSource;
    }

    public AliasRegistry getAliasRegistry() {
        return aliasRegistry;
    }

    public void setAliasRegistry(AliasRegistry aliasRegistry) {
        this.aliasRegistry = aliasRegistry;
    }

    public List<String> getMapperPathList() {
        return mapperPathList;
    }
    public Connection getConnection() {
        return pooledDataSource.getConnection();
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

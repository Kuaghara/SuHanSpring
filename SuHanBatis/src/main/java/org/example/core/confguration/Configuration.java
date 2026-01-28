package org.example.core.confguration;

import org.example.core.mapper.Mapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/// 这个类我咋不记得了？？？
/// 想起来了，mybatis的数据要传输到这里，然后再对数据进行注册啥的
public class Configuration {
    List<String> mapperPathList = new ArrayList<>();
    List<Mapper> mapperList = new ArrayList<>();
    private Environment environment;
    private PooledDataSource pooledDataSource;
    private AliasRegistry aliasRegistry = null;

    public Configuration() {
        this(new Environment(), new PooledDataSource(), new AliasRegistry());
    }

    public Configuration(Environment environment) {
        this(environment, new PooledDataSource(), new AliasRegistry());
    }

    public Configuration(Environment environment, PooledDataSource pooledDataSource) {
        this(environment, pooledDataSource, new AliasRegistry());
    }

    public Configuration(Environment environment, PooledDataSource pooledDataSource, AliasRegistry aliasRegistry) {
        this.environment = environment;
        this.pooledDataSource = pooledDataSource;
        this.aliasRegistry = aliasRegistry;
    }

    public void addMappers(String packagePath) {
        mapperPathList.add(packagePath);
    }

    public void addMapper(Mapper mapper) {
        mapperList.add(mapper);
    }

    public List<Mapper> getMapperList() {
        return mapperList;
    }

    public AliasRegistry getTypeAliasRegistry() {
        return aliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        if (environment.getDataSource() instanceof PooledDataSource) {
            this.pooledDataSource = (PooledDataSource) environment.getDataSource();
        }
    }

    public PooledDataSource getPooledDataSource() {
        return pooledDataSource;
    }

    public void setPooledDataSource(PooledDataSource pooledDataSource) {
        this.pooledDataSource = pooledDataSource;
        if (this.environment != null) {
            this.environment.setDataSource(pooledDataSource);
        }
    }

    public void setDataSource(javax.sql.DataSource dataSource) {
        if (dataSource instanceof PooledDataSource) {
            this.pooledDataSource = (PooledDataSource) dataSource;
            if (this.environment != null) {
                this.environment.setDataSource(dataSource);
            }
        }
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
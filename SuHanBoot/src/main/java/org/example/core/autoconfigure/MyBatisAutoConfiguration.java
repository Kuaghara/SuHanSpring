package org.example.core.autoconfigure;

import org.example.core.annotation.Bean;
import org.example.core.annotation.ConditionalOnClass;
import org.example.core.annotation.ConditionalOnMissingBean;
import org.example.core.annotation.Configuration;
import org.example.core.sqlsession.SqlSessionFactory;
import org.example.core.sqlsession.SqlSessionFactoryBuilder;
import org.example.core.confguration.Environment;
import org.example.core.transactionFactory.JdbcTransactionFactory;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(name = "org.example.core.sqlsession.SqlSessionFactory")
public class MyBatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // 使用配置构建SqlSessionFactory
        org.example.core.confguration.Configuration configuration =
            new org.example.core.confguration.Configuration();
        configuration.setPooledDataSource((org.example.core.confguration.PooledDataSource) dataSource);
        Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        
        return new SqlSessionFactoryBuilder().build(configuration);
    }
}

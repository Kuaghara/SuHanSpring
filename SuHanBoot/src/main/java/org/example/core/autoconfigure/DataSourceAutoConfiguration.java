package org.example.core.autoconfigure;

import org.example.core.annotation.Bean;
import org.example.core.annotation.ConditionalOnClass;
import org.example.core.annotation.Configuration;
import org.example.core.annotation.ConditionalOnMissingBean;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(name = "javax.sql.DataSource")
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        // 创建一个简单的数据源实现
        return new org.example.core.confguration.PooledDataSource();
    }
}
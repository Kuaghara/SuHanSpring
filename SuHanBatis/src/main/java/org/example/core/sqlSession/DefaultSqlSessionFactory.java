package org.example.core.sqlSession;

import org.example.core.confguration.Configuration;

import java.sql.Connection;

public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration config;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.config = configuration;
    }
    @Override
    public SqlSession openSession() {
        return openSession(true);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return new DefaultSqlSession(config);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public SqlSession openSession(String transactionIsolationLevel) {
        return openSession(transactionIsolationLevel, true);
    }

    @Override
    public SqlSession openSession(String transactionIsolationLevel, boolean autoCommit) {
        return null;
    }

    @Override
    public Configuration getConfig() {
        return config;
    }
}

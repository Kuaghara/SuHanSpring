package org.example.core.sqlsession;

import org.example.core.confguration.Configuration;

import java.sql.Connection;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration config;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.config = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSession(null, true);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSession("", autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return new DefaultSqlSession(config, connection);
    }

    @Override
    public SqlSession openSession(String transactionIsolationLevel) {
        return openSession(transactionIsolationLevel, true);
    }

    @Override
    public SqlSession openSession(String transactionIsolationLevel, boolean autoCommit) {
        return new DefaultSqlSession(config, config.getConnection(), transactionIsolationLevel, autoCommit);
    }

    @Override
    public Configuration getConfig() {
        return config;
    }
}

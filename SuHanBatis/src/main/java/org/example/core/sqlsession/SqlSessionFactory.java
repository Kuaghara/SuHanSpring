package org.example.core.sqlsession;

import org.example.core.confguration.Configuration;

import java.sql.Connection;

public interface SqlSessionFactory {
    SqlSession openSession();
    SqlSession openSession(boolean autoCommit);
    SqlSession openSession(Connection connection);
    SqlSession openSession(String transactionIsolationLevel);
    SqlSession openSession(String transactionIsolationLevel, boolean autoCommit);
    Configuration getConfig();
}

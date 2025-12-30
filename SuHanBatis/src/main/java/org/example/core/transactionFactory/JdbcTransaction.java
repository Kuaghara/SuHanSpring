package org.example.core.transactionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction{
    private Connection connection;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }
    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }
}

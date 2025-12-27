package org.example.core.transactionFactory;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

public class JdbcTransactionFactory implements TransactionFactory{

    Map<String ,String> props;
    @Override
    public void setProperties(Map<String , String> props) {
        this.props = props;
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }
}

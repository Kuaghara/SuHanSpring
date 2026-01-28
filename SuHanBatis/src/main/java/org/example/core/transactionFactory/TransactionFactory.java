package org.example.core.transactionFactory;

import java.sql.Connection;
import java.util.Map;


public interface TransactionFactory {
    default void setProperties(Map<String, String> props) {
    }

    Transaction newTransaction(Connection conn);

    //Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}

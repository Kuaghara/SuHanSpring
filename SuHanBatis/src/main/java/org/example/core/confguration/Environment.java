package org.example.core.confguration;

import org.example.core.transactionFactory.TransactionFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

public class Environment {
    String id;
    TransactionFactory transactionFactory;
    DataSource dataSource;
    Map<String ,  String> configResource;

    public Environment() {
    }

    public Environment(String id , TransactionFactory transactionFactory , DataSource dataSource ){
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public void setConfigResource(Map<String , String> configResource) {
        this.configResource = configResource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, String> getConfigResource() {
        return configResource;
    }
}

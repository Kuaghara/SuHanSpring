package org.example.core.confguration;

import java.sql.SQLData;

public class PooledDataSource {

    String driver;
    String url;
    String username;
    String password;

    PooledDataSource(String driver, String url, String username, String password){
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }
}

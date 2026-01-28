package org.example.core.confguration;

import java.sql.*;

//能看出这是干啥的，但是我写这个只是为了写pooledDataSource的api
public class JdbcConnectionBuilder implements ConnectionBuilder {

    ShardingKey shardingKey; //这俩啥啊？
    ShardingKey superShardingKey;
    private String username;
    private String password;

    @Override
    public ConnectionBuilder user(String username) {
        this.username = username;
        return this;
    }

    @Override
    public ConnectionBuilder password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public ConnectionBuilder shardingKey(ShardingKey shardingKey) {
        this.shardingKey = shardingKey;
        return this;
    }

    @Override
    public ConnectionBuilder superShardingKey(ShardingKey superShardingKey) {
        this.superShardingKey = superShardingKey;
        return this;
    }

    @Override
    public Connection build() throws SQLException {
        return DriverManager.getConnection("com.mysql.cj.jdbd.Driver", username, password);
    }
}

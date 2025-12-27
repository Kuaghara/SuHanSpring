package org.example.core.confguration;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {

    private static ConnectionBuilder connectionBuilder = new JdbcConnectionBuilder();

    Connection connection;
    String driver;
    String url;
    String username;
    String password;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5,  //核心线程5
            20,            //最大线程20
            10L,           //线程空闲时间10秒
            TimeUnit.SECONDS,  //时间单位
            new LinkedBlockingQueue<>(100), //任务队列为阻塞队列
            Executors.defaultThreadFactory(),      //线程工厂
            new ThreadPoolExecutor.CallerRunsPolicy()   //拒绝策略
    );

    public PooledDataSource(){

    }

    PooledDataSource(String driver, String url, String username, String password){
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        openConnection();
    }

    private void openConnection(){
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Connection getConnection(){
        return connection;
    }

    @Override
    public ConnectionBuilder createConnectionBuilder()  {
        return connectionBuilder;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        connectionBuilder.user(username).password(password);
        return connectionBuilder.build();
    }

    ///后面方法先空置，不清楚干什么的
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return DataSource.super.createShardingKeyBuilder();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

package org.example.core.sqlsession;

import org.example.core.confguration.Configuration;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/// 先粘点接口用，后面根据实现情况删除不用的
public interface SqlSession extends AutoCloseable {
    <T> T selectOne(String statement);

    <T> T selectOne(String statement, Object parameter);

    <E> List<E> selectList(String statement);

    <E> List<E> selectList(String statement, Object parameter);

    //<E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

    <K, V> Map<K, V> selectMap(String statement, String mapKey);

    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

    //<K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

    //<T> Cursor<T> selectCursor(String statement);

    //<T> Cursor<T> selectCursor(String statement, Object parameter);

    //<T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

    //void select(String statement, Object parameter, ResultHandler handler);

    //void select(String statement, ResultHandler handler);

   // void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler);

    int insert(String statement);

    int insert(String statement, Object parameter);

    int update(String statement);

    int update(String statement, Object parameter);

    int delete(String statement);

    int delete(String statement, Object parameter);

    void commit();

    void commit(boolean force);

    void rollback();

    void rollback(boolean force);

    List<BatchResult> flushStatements();

    void close();

    void clearCache();

    Configuration getConfiguration();

    <T> T getMapper(Class<T> type);

    Connection getConnection();



}

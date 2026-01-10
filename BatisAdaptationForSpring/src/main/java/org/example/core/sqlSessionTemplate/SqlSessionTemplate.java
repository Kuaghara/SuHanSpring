package org.example.core.sqlSessionTemplate;

import org.example.core.annotation.Order;
import org.example.core.confguration.Configuration;
import org.example.core.sqlsession.BatchResult;
import org.example.core.sqlsession.SqlSession;
import org.example.core.sqlsession.SqlSessionFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/// 虽然这只是一个套壳，但是为了适配这个我特地去修了我spring代码的bug
@Order(998)
public class SqlSessionTemplate implements SqlSession {
    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSession sqlSession;

    public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        sqlSession = sqlSessionFactory.openSession();
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @Override
    public <T> T selectOne(String statement) {
        return sqlSession.selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return sqlSession.selectOne(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return sqlSession.selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return sqlSession.selectList(statement, parameter);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return sqlSession.selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return sqlSession.selectMap(statement, parameter, mapKey);
    }

    @Override
    public int insert(String statement) {
        return sqlSession.insert(statement);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return sqlSession.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return sqlSession.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return sqlSession.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return sqlSession.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return sqlSession.delete(statement, parameter);
    }

    @Override
    public void commit() {
        sqlSession.commit();
    }

    @Override
    public void commit(boolean force) {
        sqlSession.commit(force);
    }

    @Override
    public void rollback() {
        sqlSession.rollback();
    }

    @Override
    public void rollback(boolean force) {
        sqlSession.rollback(force);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return sqlSession.flushStatements();
    }

    @Override
    public void close() {
        sqlSession.close();
    }

    @Override
    public void clearCache() {
        sqlSession.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return sqlSession.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return sqlSession.getMapper(type);
    }

    @Override
    public Connection getConnection() {
        return sqlSession.getConnection();
    }
}

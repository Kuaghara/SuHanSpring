package org.example.core.sqlSession;

import org.example.core.confguration.Configuration;
import org.example.core.handle.HandlerManager;
import org.example.core.handle.ObjectHandler;
import org.example.core.handle.ParameterHandler;
import org.example.core.handle.StatementHandler;
import org.example.mapper.Mapper;
import org.example.mapper.MapperStatement;

import java.sql.*;
import java.util.List;
import java.util.Map;


public class DefaultSqlSession implements SqlSession{

    Configuration config;
    Connection connection ;

    HandlerManager handlerManager = HandlerManager.getHandlerManager();
    StatementHandler statementHandler = handlerManager.getStatementHandler();
    ObjectHandler objectHandler = handlerManager.getObjectHandler();
    ParameterHandler parameterHandler = handlerManager.getParameterHandler();

    public DefaultSqlSession(Configuration config) {
        this.config = config;
        connection = config.getConnection();
    }

    //把Statement对象从原来的字符串挑出来
    private MapperStatement getStatement(String statement){
        List<Mapper> mapperList = config.getMapperList();
        MapperStatement mapperStatement = null;
        for(Mapper mapper : mapperList){
            if(mapperStatement != null){
                throw new RuntimeException("在不同的mapper文件中存在相同id的sql语句");
            }
            mapperStatement = mapper.getMapperStatement(statement);
        }
        return mapperStatement;
    }

    @Override
    public <T> T selectOne(String statement) {
        return this.selectOne(statement, (Object) null);
    }

    @Override
    //1.获取一个sql语句对应的Statement对象
    //2.获取其传入的参数
    //3.对参数进行判断（parameterType.class == parameter(下方的形参).class）
    //4.执行sql语句
    //5.1获取返回对象类型
    //6.1通过反射进行创建对象以及赋值
    //5.2获取对应的resultMap类型
    //6.2通过反射进行创建对象
    //7.1根据映射关系给对象进行赋值
    //8.返回对象
    //关于创建对象那一步，应该还要对alias进行映射
    public <T> T selectOne(String statementStr, Object parameter) {
        return (T) selectList(statementStr, parameter).get(0);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return selectList(statement, null);
    }

    @Override
    public <E> List<E> selectList(String statementStr, Object parameter)  {
        MapperStatement statementObj = getStatement(statementStr);
        if( parameterHandler.isTypeMatch(statementObj.getParameterType(), parameter)){
            //执行sql语句，得到结果集
            ResultSet resultSet = statementHandler.handleSQLStatementsForSelect(statementObj.getSql(), connection, parameter);

            //对结果集进行反射创建对象赋值
            return objectHandler.handleResultForList(resultSet,statementObj,config);
        }
        return null;
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
       return selectMap(statement, null, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        MapperStatement statementObj = getStatement(statement);
        if( parameterHandler.isTypeMatch(statementObj.getParameterType(), parameter)){
            //执行sql语句，得到结果集
            ResultSet resultSet = statementHandler.handleSQLStatementsForSelect(statementObj.getSql(), connection, parameter);

            //对结果集进行反射创建对象赋值
            return objectHandler.handleResultForMap(resultSet,statementObj,config,mapKey);
        }
        return null;
    }

    @Override
    public int insert(String statement) {
        return insert(statement, null);
    }

    @Override
    public int insert(String statement, Object parameter) {
        MapperStatement statementObj = getStatement(statement);
        if( parameterHandler.isTypeMatch(statementObj.getParameterType(), parameter)){
            if(statementObj.isUseGeneratedKeys()){
                //执行sql语句，得到结果集
                ResultSet resultSet = statementHandler.handleSQLStatementsForSelect(statementObj.getSql(), connection, parameter);

                //对结果集进行反射创建对象赋值
                return objectHandler.handleResultForList(resultSet,statementObj,config).size();
            }else {
                int i = statementHandler.handleSQLStatementsForDML(statementObj, connection, parameter);
                return i;
            }
        }
        return 0;
    }

    @Override
    public int update(String statement) {
        return update(statement, null);
    }

    @Override
    public int update(String statement, Object parameter) {
        return 0;
    }

    @Override
    public int delete(String statement) {
        return delete(statement, null);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return 0;
    }

    @Override
    public void commit() {

    }

    @Override
    public void commit(boolean force) {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void rollback(boolean force) {

    }

    @Override
    public List<BatchResult> flushStatements() {
        return List.of();
    }

    @Override
    public void close() {

    }

    @Override
    public void clearCache() {

    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}

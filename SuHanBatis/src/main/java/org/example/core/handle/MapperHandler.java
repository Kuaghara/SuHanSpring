package org.example.core.handle;

import org.example.core.confguration.Configuration;
import org.example.core.mapper.Mapper;
import org.example.core.mapper.MapperStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class MapperHandler implements InvocationHandler {
    Configuration config;
    private final StatementHandler statementHandler = HandlerManager.getHandlerManager().getStatementHandler();
    private final ParameterHandler parameterHandler = HandlerManager.getHandlerManager().getParameterHandler();
    private final ObjectHandler objectHandler = HandlerManager.getHandlerManager().getObjectHandler();

    public MapperHandler setConfig(Configuration config) {
        this.config = config;
        return this;
    }

    //1.判断是否为接口
    //2.判断与配置文件中的mapper是否一致
    //3.获取该类中所有的方法
    //4.将方法名字与mapper中的id进行匹配
    //5.判断sql语句类型
    //6.判断参数类型及获取参数
    //7.执行对应的sql语句，获取结果
//    public <T> T handleMapper(Configuration config, Class<T> ife, Connection connection) {
//        List<Mapper> mapperList = config.getMapperList();
//        for (Mapper mappers : mapperList) {
//            try {
//                Class<?> interfaceClazz = Class.forName(mappers.getNamespace());
//                //判断是否为相同类型
//                if (interfaceClazz.isAssignableFrom(ife)) {
//                    Method[] methods = ife.getDeclaredMethods();
//                    for (Method method : methods) {
//                        String name = method.getName();
//                        MapperStatement ms = mappers.getMapperStatement(name);
//                        if (ms != null) {
//                            String sql = ms.getSql();
//                            //去获取参数
//                            Object parameter = parameterHandler.readTheShapeParameter(method);
//                            if (sql.contains("select") || sql.contains("SELECT")) {
//
//                                ResultSet resultSet = statementHandler.handleSQLStatementsForSelect(sql, connection, );
//                            }
//                        }
//
//                    }
//                }
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//        return null;
//    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        List<Mapper> mapperList = config.getMapperList();
        for(Mapper mapper : mapperList) {
            MapperStatement ms = mapper.getMapperStatement(methodName);

            if (ms == null) {
                throw new RuntimeException("请检查mapper中的id是否一致");
            }

            Map<String, Object> parameterMap = parameterHandler.readTheShapeParameter(method, args);
            if (ms.getSql().contains("select") || ms.getSql().contains("SELECT")) {
                //要将方法中的@Param注解搞成一个map作为参数传入
                //执行sql语句，得到结果集
                ResultSet resultSet = statementHandler.handleSQLStatementsForSelect(ms.getSql(), config.getConnection(), parameterMap);
                //对结果集进行反射创建对象赋值
                List<Object> objects = objectHandler.handleResultForList(resultSet, ms, config);
                if(objects.size() != 1){
                    return objects;
                }
                else return objects.get(0);
            } else {
                if (ms.isUseGeneratedKeys()) {
                    //执行sql语句，得到结果集
                    int l = statementHandler.handleSQLStatementsForDMLWithGeneratedKeys(ms, config.getConnection(), parameterMap);
                    return l;
                } else {
                    int i = statementHandler.handleSQLStatementsForDML(ms, config.getConnection(), parameterMap);
                    return i;
                }
            }
        };
        return null;
    }
}

package org.example.core.handle;

import org.example.core.confguration.XmlStatementBuilder;
import org.example.mapper.MapperStatement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;
import java.util.Map;

/// 用于对进入的sql语句进行具体的处理
public class StatementHandler {
    private final XmlStatementBuilder xmlStatementBuilder = new XmlStatementBuilder();

    //把xml中的sql语句拆成preperedStatement中对应的sql语句，然后再创造一个preparedStatement对象，然后进行参数设置
    // 返回一个结果集
    //写一个这个可费死劲了
    public ResultSet handleSQLStatementsForSelect(String sql, Connection connection, Object parameter) {
        String ps = xmlStatementBuilder.convertToPreparedStatementSQL(sql).getFirst();
        try {
            PreparedStatement preStatement = connection.prepareStatement(ps);
            if (Map.class.isAssignableFrom(parameter.getClass())) {
                Map<String, Object> parameter1 = (Map<String, Object>) parameter;
                int i = 1; // 参数索引
                for (Map.Entry<String, Object> entry : parameter1.entrySet()) {
                    setParameters(preStatement, entry.getValue(), i++);
                }
            } else {
                setParameters(preStatement, parameter, 1);
            }
            ResultSet resultSet = preStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParameters(PreparedStatement preStatement, Object parameter, int index) throws SQLException {
        switch (parameter) {
            case Integer i -> preStatement.setInt(index, (int) parameter);
            case String s -> preStatement.setString(index, (String) parameter);
            case Float f -> preStatement.setFloat(index, (float) parameter);
            case Double d -> preStatement.setDouble(index, (double) parameter);
            case Boolean b -> preStatement.setBoolean(index, (boolean) parameter);
            case Character c -> preStatement.setString(index, (String) parameter);
            default -> throw new RuntimeException("不支持的参数类型");
        }
    }

    public int handleSQLStatementsForDML(MapperStatement sta, Connection connection, Object parameter) {
        List<String> strs = xmlStatementBuilder.convertToPreparedStatementSQL(sta.getSql()); // 获取sql语句及参数
        String ps = strs.getFirst();
        try {
            PreparedStatement preStatement = connection.prepareStatement(ps);
            if (Map.class.isAssignableFrom(parameter.getClass())) {
                Map<String, Object> parameter1 = (Map<String, Object>) parameter;
                int i = 1; // 参数索引
                for (Map.Entry<String, Object> entry : parameter1.entrySet()) {
                    if (entry.getKey().equals(strs.get(i))) {
                        setParameters(preStatement, entry.getValue(), i++);
                    } else {
                        return 0;
                    }
                }
            } else {
                Class<?> clazz = parameter.getClass();
                Field[] fields = clazz.getDeclaredFields();

                for (int i = 1; i < strs.size(); ) {
                    for (Field field : fields) {
                        if (field.getName().equals(strs.get(i))) {
                            field.setAccessible(true);
                            setParameters(preStatement, field.get(parameter), i++);
                            break;
                        }
                    }
                }
            }
            return preStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int handleSQLStatementsForDMLWithGeneratedKeys(MapperStatement sta, Connection connection, Object parameter) {
        List<String> strs = xmlStatementBuilder.convertToPreparedStatementSQL(sta.getSql()); // 获取sql语句及参数
        String ps = strs.getFirst();
        try {
            PreparedStatement preStatement = connection.prepareStatement(ps, Statement.RETURN_GENERATED_KEYS);

            if (Map.class.isAssignableFrom(parameter.getClass())) {
                Map<String, Object> parameter1 = (Map<String, Object>) parameter;
                //1.在传入参数的key与sql语句中的key不匹配时，报错，无法执行sql语句
                //2.假如在使用自动主键的情况下，主键又作为参数传入，无视传入的主键，
                for (int i = 1; i < strs.size(); i++) {
                    Object value = parameter1.get(strs.get(i));
                    if (value == null) {
                        throw new RuntimeException("映射关系错误");
                    }
                    setParameters(preStatement, value, i);
                }
            } else {
                //1.在使用主键后，传入主键数值，无视
                //2.mapper中的 keyProperty 与 实体类中的属性名不同时，执行sql语句但是报错
                //3.mapper中的 keyProperty 与 sql语句中的名字不同时，报错且不执行sql语句
                //对于第二条，ai说mybatis会在完成之后将结果重新赋值给变量，神经病啊
                Class<?> clazz = parameter.getClass();

                for (int i = 1; i < strs.size(); ) {
                    Method getMethod = clazz.getMethod("get" + Character.toUpperCase(strs.get(i).charAt(0)) + strs.get(i).substring(1));
                    Object value = getMethod.invoke(parameter);
                    setParameters(preStatement, value, i++);
                }
            }
            return preStatement.executeUpdate();
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

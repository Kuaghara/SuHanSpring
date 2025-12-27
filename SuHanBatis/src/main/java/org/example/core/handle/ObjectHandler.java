package org.example.core.handle;

import org.example.core.confguration.AliasRegistry;
import org.example.core.confguration.Configuration;
import org.example.core.confguration.XmlMapperBuilder;
import org.example.mapper.Mapper;
import org.example.mapper.MapperStatement;
import org.example.mapper.ResultMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectHandler {
    private XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder();

    public ObjectHandler(){}
    //在这里我还得想办法处理一下别名
    public<T> List<T> handleResultForList(ResultSet resultSet , MapperStatement statementObj, Configuration config){
        AliasRegistry aliasRegistry = config.getAliasRegistry();
        //先判断有没有resultMap
        if(statementObj.getResultMap().isEmpty()){
            String resultType = statementObj.getResultType();
            //再判断有没有别名
            String packageName = aliasRegistry.getPackageName(resultType);
            if(packageName == null) {
                packageName = resultType;
            }
            //然后开始创建对象，去获取class对象然后进行反射创建
            try {
                Class<?> resultClazz = Class.forName(packageName);
                Constructor<?>[] constructors = resultClazz.getConstructors();
                Constructor<?> theConstructor;
                if (!(constructors.length == 1)) throw new RuntimeException("多个构造方法");

                theConstructor = constructors[0];
                Object[] args = new Object[0];
                List<T> resultList = new ArrayList<>();

                while(resultSet.next()) {
                    Object bean;
                    if (theConstructor.getParameterCount() == 0) {
                        bean = theConstructor.newInstance();
                        T obj = setProperties(bean, resultClazz, resultSet ,null);
                        resultList.add(obj);
                    } else {
                        //对参数数组进行一次判断，看看有没有经历过查询获取默认参数值
                        if(args.length == 0) {
                            args = getParameterConstructorArgs(theConstructor);
                        }
                        bean = theConstructor.newInstance(args);
                        T obj = setProperties(bean, resultClazz, resultSet ,null);
                        resultList.add(obj);
                    }
                }
                return resultList;
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            //此时的resultMap还没有从Mapper里挑出来（
            String resultMap = statementObj.getResultMap();
            Mapper mapper = statementObj.getMapper();
            ResultMap resultMapObj = mapper.getResultMap(resultMap);

            //然后从别名中获取返回对象的名字
            String type = resultMapObj.getType();
            String packageName = aliasRegistry.getPackageName(type);
            if(packageName.isEmpty()) {
                packageName = type;
            }

            //和上面一样开始创建对象什么的（
            try {
                Class<?> resultClazz = Class.forName(packageName);
                Constructor<?>[] constructors = resultClazz.getConstructors();
                Constructor<?> theConstructor;
                if (!(constructors.length == 1)) throw new RuntimeException("多个构造方法");

                theConstructor = constructors[0];
                Object[] args = new Object[0];
                List<T> resultList = new ArrayList<>();

                while(resultSet.next()) {
                    Object bean;
                    if (theConstructor.getParameterCount() == 0) {
                        bean = theConstructor.newInstance();
                    } else {
                        //对参数数组进行一次判断，看看有没有经历过查询获取默认参数值
                        if(args.length == 0) {
                            args = getParameterConstructorArgs(theConstructor);
                        }
                        bean = theConstructor.newInstance(args);
                        T obj = setProperties(bean, resultClazz, resultSet ,resultMapObj);
                        resultList.add(obj);
                    }
                }
                return resultList;
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //获取构造方法中参数的各种默认值，然后进行反射创建对象
    private Object[] getParameterConstructorArgs(Constructor<?> constructor){
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++){
            if (parameterTypes[i] == String.class ||parameterTypes[i] == char.class || parameterTypes[i] == Character.class){
                args[i] = " ";
            }
            else if (parameterTypes[i] == int.class || parameterTypes[i] == Integer.class){
                args[i] = 0;
            } else if (parameterTypes[i] == float.class || parameterTypes[i] == Float.class) {
                args[i] = 0.0f;
            }
            else if (parameterTypes[i] == double.class || parameterTypes[i] == Double.class) {
                args[i] = 0.0;
            }
            else if (parameterTypes[i] == boolean.class || parameterTypes[i] == Boolean.class) {
                args[i] = false;
            }
            else if (parameterTypes[i] == long.class || parameterTypes[i] == Long.class) {
                args[i] = 0L;
            }
            else  {
                args[i] = null;
            }
        }
        return args;
    }

    //反射创建对象后，将数据库中查找到的信息属性列名与实体类中的属性名进行匹配，能匹配上就赋值
    //无法匹配就不管，为空（我给他们赋值了默认值），此处还得开始考虑resultMap的事情了（
    private <T> T setProperties(Object bean ,Class<?> clazz , ResultSet resultSet ,ResultMap resultMap) throws SQLException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        if(resultMap == null) {
            try {
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object columnObj = resultSet.getObject(i);
                    for (Field field : fields) {
                        String propertyName = field.getName();

                        if (propertyName.equals(columnName)) {
                            field.setAccessible(true);
                            Object value = xmlMapperBuilder.convertType(columnObj,field.getClass());
                            field.set(bean, value);
                        }
                    }
                }
                return (T) bean;
            }
            catch (SQLException e){
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e){
                throw new RuntimeException("数据库内类型与实体类中类型并不相同");
            }
        }
        //经过实测，在resultMap中
        //假如映射关系少写了，不会报错，只是对应的关系为null
        //假如映射关系中property(实体类中的名称)写错了，会报错
        //假如实体类中主键对应属性的名称写错了，会报错
        //假如实体类中属性名写错了，没报错且正确赋值了？？？ 此处卡莫说是糟粕，不要学
        //假设映射关系中column(数据库中的名称)写错了，对应的关系为null
        else {
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnNameInDatabase = resultSet.getMetaData().getColumnName(i);
                Object columnObj = resultSet.getObject(i);
                boolean idIsFound = false;
                for (Field field : fields) {
                    String propertyNameInEntity = field.getName();
                    if(!idIsFound && propertyNameInEntity.equals(resultMap.getIdPropertyInXml())){
                        if(!propertyNameInEntity.equals(resultMap.getIdColumnInXml())){
                            throw new RuntimeException("实体类中主键属性名与xml中属性名不一致");
                        }
                        field.setAccessible(true);
                        Object value = xmlMapperBuilder.convertType(columnObj,field.getClass());
                        field.set(bean, value);
                        idIsFound = true;
                    }

                    if (propertyNameInEntity.equals(resultMap.getResultMap().get(columnNameInDatabase))) {
                        if(!propertyNameInEntity.equals(resultMap.getIdPropertyInXml())){
                            throw new RuntimeException("实体类中属性名与xml中属性名不一致");
                        }
                        field.setAccessible(true);
                        Object value = xmlMapperBuilder.convertType(columnObj,field.getClass());
                        field.set(bean, value);
                    }
                }
            }
            return (T) bean;
        }
    }
    /*统计下流程和需要的数据
    * 1.result的列数->用于划定一层循环的范围
    * 2.result每一列的列明->用于与实体类中的属性名进行匹配
    * 3.实体类中的属性名->用于与result每一列的列明进行匹配
    * ----------
    * 需要进行缓存的数值：
    * 1.args 实体类构造方法的参数list->避免多次查询增加效率
    * 2.result每一列的列名->避免多次查询增加效率
    * ----------
    * 当前思路：resultSet只能.next()来进行下一步，没法获取具体多少的行数
    * 1.最外层while(resultSet.next())循环
    * 2.内层进行一次创建实体类
    * 3.创建完进行赋值
    * 4.返回后加入返回的数组中
    * */

    public<K,V> Map<K,V> handleResultForMap(ResultSet resultSet , MapperStatement statementObj, Configuration config,String mapKey){
        AliasRegistry aliasRegistry = config.getAliasRegistry();
        //先判断有没有resultMap
        if(statementObj.getResultMap().isEmpty()){
            String resultType = statementObj.getResultType();
            //再判断有没有别名
            String packageName = aliasRegistry.getPackageName(resultType);
            if(packageName == null) {
                packageName = resultType;
            }
            //然后开始创建对象，去获取class对象然后进行反射创建
            try {
                Class<?> resultClazz = Class.forName(packageName);
                Constructor<?>[] constructors = resultClazz.getConstructors();
                Constructor<?> theConstructor;
                if (!(constructors.length == 1)) throw new RuntimeException("多个构造方法");

                theConstructor = constructors[0];
                Object[] args = new Object[0];
                Map<K,V> resultMap = new HashMap<>();

                while(resultSet.next()) {
                    Object bean;
                    if (theConstructor.getParameterCount() == 0) {
                        bean = theConstructor.newInstance();
                        V obj = setProperties(bean, resultClazz, resultSet ,null);
                        K key = (K) resultSet.getObject(mapKey);
                        resultMap.put(key,obj);
                    } else {
                        //对参数数组进行一次判断，看看有没有经历过查询获取默认参数值
                        if(args.length == 0) {
                            args = getParameterConstructorArgs(theConstructor);
                        }
                        bean = theConstructor.newInstance(args);
                        V obj = setProperties(bean, resultClazz, resultSet ,null);
                        K key = (K) resultSet.getObject(mapKey);
                       resultMap.put(key,obj);
                    }
                }
                return resultMap;
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            //此时的resultMap还没有从Mapper里挑出来（
            String resultMap = statementObj.getResultMap();
            Mapper mapper = statementObj.getMapper();
            ResultMap resultMapObj = mapper.getResultMap(resultMap);

            //然后从别名中获取返回对象的名字
            String type = resultMapObj.getType();
            String packageName = aliasRegistry.getPackageName(type);
            if(packageName.isEmpty()) {
                packageName = type;
            }

            //和上面一样开始创建对象什么的（
            try {
                Class<?> resultClazz = Class.forName(packageName);
                Constructor<?>[] constructors = resultClazz.getConstructors();
                Constructor<?> theConstructor;
                if (!(constructors.length == 1)) throw new RuntimeException("多个构造方法");

                theConstructor = constructors[0];
                Object[] args = new Object[0];
                Map<K ,V> rm = new HashMap<>();

                while(resultSet.next()) {
                    Object bean;
                    if (theConstructor.getParameterCount() == 0) {
                        bean = theConstructor.newInstance();
                        V obj = setProperties(bean, resultClazz, resultSet ,resultMapObj);
                        K key = (K) resultSet.getObject(mapKey);
                        rm.put(key,obj);
                    } else {
                        //对参数数组进行一次判断，看看有没有经历过查询获取默认参数值
                        if(args.length == 0) {
                            args = getParameterConstructorArgs(theConstructor);

                        }
                        bean = theConstructor.newInstance(args);
                        V obj = setProperties(bean, resultClazz, resultSet ,resultMapObj);
                        K key = (K) resultSet.getObject(mapKey);
                        rm.put(key,obj);
                    }
                }
                return rm;
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //获取构造方法中参数的各种默认值，然后进行反射创建对象

}

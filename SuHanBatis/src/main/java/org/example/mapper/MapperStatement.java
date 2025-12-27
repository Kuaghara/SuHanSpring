package org.example.mapper;

public class MapperStatement {
    //在我编写的总结中，mybatis就只包含了这四个属性
    private String id; //用来区分不同SQL语句的名字
    private String resultType; //返回结果类型
    private String parameterType; //接收的参数类型
    private String resultMap; //结果映射
    private boolean useGeneratedKeys;
    private String keyProperty;
    private String keyColumn;
    private Mapper mapper; //这里再保存下Mapper，免得忘了爹（

    private String sql; //内部的sql语句


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}

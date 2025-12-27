package org.example.mapper;

import java.util.HashMap;
import java.util.Map;

public class Mapper {
    private String namespace;
    Map<String , MapperStatement> sqls = new HashMap<>();
    Map<String , ResultMap> resultMaps = new HashMap<>();

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void addMapperStatement(String id , MapperStatement mapperStatement){
        if(sqls.get(id) != null){
            throw new RuntimeException("存在id相同的sql语句");
        }
        sqls.put(mapperStatement.getId(), mapperStatement);
    }
    public void addResultMap(String id,ResultMap resultMap){
        if(resultMaps.get(resultMap.getId()) != null){
            throw new RuntimeException("存在id相同的resultMap");
        }
        resultMaps.put(resultMap.getId(), resultMap);
    }
    public MapperStatement getMapperStatement(String id){
        return sqls.get(id);
    }
    public ResultMap getResultMap(String id){
        return resultMaps.get(id);
    }
}

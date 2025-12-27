package org.example.mapper;

import java.util.HashMap;
import java.util.Map;

public class ResultMap {
    String id; //区分不同resultMap的名字
    String type; //映射的实体类
    String idPropertyInXml; // 标签id中实体类的属性名
    String idColumnInXml; //标签id中数据库的列名
    Map<String , String > result = new HashMap<>(); //用于存储result标签的映射关系

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdPropertyInXml() {
        return idPropertyInXml;
    }

    public void setIdPropertyInXml(String idPropertyInXml) {
        this.idPropertyInXml = idPropertyInXml;
    }

    public String getIdColumnInXml() {
        return idColumnInXml;
    }

    public void setIdColumnInXml(String idColumnInXml) {
        this.idColumnInXml = idColumnInXml;
    }

    public Map<String, String> getResultMap() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}

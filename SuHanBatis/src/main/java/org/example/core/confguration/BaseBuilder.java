package org.example.core.confguration;

import org.example.core.mapper.Mapper;
import org.example.core.mapper.MapperStatement;
import org.example.core.mapper.ResultMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseBuilder {
    protected Configuration configuration = new Configuration();

    DocumentBuilderFactory factory;
    DocumentBuilder documentBuilder;

    /*
     * 此处粘贴一下ai对于该类的作用的解答：
     * 1.Configuration 引用 √
     * 2.类型解析方法：
     *   解析别名类型
     *   解析类名到Class对象
     *   处理类型转换
     * 3.属性处理方法
     *   解析XML属性 √
     *   处理属性占位符 √
     *   验证必须属性 √
     * 4.通用工具方法：
     *   字符串处理 √
     *   错误报告 不会写
     *   日志记录 不会写
     *
     * 继承体系    内啥，我感觉相应的工作我都在这个里面写完了欸（
     * BaseBuilder 通常是以下构建器类的父类：
     * XMLConfigBuilder - 解析 MyBatis 主配置文件
     * XMLMapperBuilder - 解析 Mapper XML 文件
     * XmlStatementBuilder - 解析 SQL 语句
     * MapperAnnotationBuilder - 解析注解形式的 Mapper
     * */

    BaseBuilder() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, String> getXmlConfigResources(FileInputStream config) {
        Map<String, String> resources = new HashMap<>();

        try {
            Document parsed = documentBuilder.parse(config);
            Element configuration = parsed.getDocumentElement();
            Element environments = (Element) configuration.getElementsByTagName("environments").item(0);
            resources.put("environments", environments.getAttribute("default"));

            NodeList environment = environments.getElementsByTagName("environment");
            for (int i = 0; i < environment.getLength(); i++) {
                Element node = (Element) environment.item(i);
                resources.put("environment" + i, node.getAttribute("id"));

                Element transactionManager = (Element) node.getElementsByTagName("transactionManager").item(0);
                resources.put("transactionManager" + i, transactionManager.getAttribute("type"));

                Element dataSource = (Element) node.getElementsByTagName("dataSource").item(0);
                resources.put("dataSource" + i, dataSource.getAttribute("type"));

                NodeList propertyNodes = dataSource.getElementsByTagName("property");
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Element node1 = (Element) propertyNodes.item(j);
                    resources.put(node1.getAttribute("name") + i, node1.getAttribute("value"));
                }
            }
            //对mapper解析
            Element mappers = (Element) configuration.getElementsByTagName("mappers").item(0);
            NodeList mapperNodes = mappers.getElementsByTagName("mapper");
            for (int i = 0; i < mapperNodes.getLength(); i++) {
                Element node = (Element) mapperNodes.item(i);
                String resource = node.getAttribute("resource");
                if (resource.isEmpty()) {
                    resource = node.getAttribute("url");
                }
                resources.put("mapper" + i, resource);
            }
            //对alias解析
            NodeList typeAliases = configuration.getElementsByTagName("typeAliases");
            for (int i = 0; i < typeAliases.getLength(); i++) {
                Element node = (Element) typeAliases.item(i);
                resources.put(node.getAttribute("type"), node.getAttribute("alias"));
            }
            return resources;
        } catch (SAXException | IOException e) {
            throw new RuntimeException("在解析mybatis配置文件时报错，此处为BaseBuilder");
        }
    }

    //在经过深思熟虑后，打算将数据统一存储到一个Mapper类中，之后传递将统一传递这一个Mapper类
    public Mapper getXmlMapperResources(FileInputStream mapperFile) {
        Mapper mapper = new Mapper();
        try {
            //获取mapper的名字
            Document parsed = documentBuilder.parse(mapperFile);
            Element mapperNode = parsed.getDocumentElement();
            String namespace = mapperNode.getAttribute("namespace");
            mapper.setNamespace(namespace);

            NodeList selectNodes = mapperNode.getElementsByTagName("select");
            for (int i = 0; i < selectNodes.getLength(); i++) {
                Element node = (Element) selectNodes.item(i);

                //保存“select”相关信息的逻辑
                MapperStatement select = new MapperStatement();
                String id = node.getAttribute("id");
                select.setId(id);
                select.setParameterType(node.getAttribute("parameterType"));
                select.setResultType(node.getAttribute("resultType"));
                select.setResultMap(node.getAttribute("resultMap"));
                select.setSql(node.getTextContent());
                select.setMapper(mapper);
                mapper.addMapperStatement(id, select);
            }

            NodeList updateNodes = mapperNode.getElementsByTagName("update");
            for (int i = 0; i < updateNodes.getLength(); i++) {
                Element node = (Element) updateNodes.item(i);

                //保存“insert”和“update”相关信息的逻辑
                MapperStatement mapperStatement = new MapperStatement();
                String id = node.getAttribute("id");
                mapperStatement.setId(id);
                mapperStatement.setUseGeneratedKeys(node.getAttribute("useGeneratedKeys").equals("true"));
                mapperStatement.setKeyProperty(node.getAttribute("keyProperty"));
                mapperStatement.setKeyColumn(node.getAttribute("keyColumn"));
                mapperStatement.setParameterType(node.getAttribute("parameterType"));
                mapperStatement.setSql(node.getTextContent());
                mapperStatement.setMapper(mapper);
                mapper.addMapperStatement(id, mapperStatement);
            }

            NodeList insertNodes = mapperNode.getElementsByTagName("insert");
            for (int i = 0; i < insertNodes.getLength(); i++) {
                Element node = (Element) insertNodes.item(i);

                //保存“insert”和“update”相关信息的逻辑
                MapperStatement mapperStatement = new MapperStatement();
                String id = node.getAttribute("id");
                mapperStatement.setId(id);
                mapperStatement.setUseGeneratedKeys(node.getAttribute("useGeneratedKeys").equals("true"));
                mapperStatement.setKeyProperty(node.getAttribute("keyProperty"));
                mapperStatement.setKeyColumn(node.getAttribute("keyColumn"));
                mapperStatement.setParameterType(node.getAttribute("parameterType"));
                mapperStatement.setSql(node.getTextContent());
                mapperStatement.setMapper(mapper);
                mapper.addMapperStatement(id, mapperStatement);

            }

            NodeList deleteNodes = mapperNode.getElementsByTagName("delete");
            for (int i = 0; i < deleteNodes.getLength(); i++) {
                Element node = (Element) deleteNodes.item(i);

                //保存“delete”相关信息的逻辑

                MapperStatement delete = new MapperStatement();
                String id = node.getAttribute("id");
                delete.setId(id);
                delete.setParameterType(node.getAttribute("parameterType"));
                delete.setMapper(mapper);
                delete.setSql(node.getTextContent());
                mapper.addMapperStatement(id, delete);

            }
            NodeList resultMapNodes = mapperNode.getElementsByTagName("resultMap");
            for (int i = 0; i < resultMapNodes.getLength(); i++) {
                Element node = (Element) resultMapNodes.item(i);

                //保存“resultMap”相关信息的逻辑
                ResultMap resultMap = new ResultMap();
                Map<String, String> map = resultMap.getResultMap();
                String id = node.getAttribute("id");
                resultMap.setId(id);
                resultMap.setType(node.getAttribute("type"));
                NodeList resultNode = node.getElementsByTagName("result");
                for (int j = 0; j < resultNode.getLength(); j++) {
                    Element node1 = (Element) resultNode.item(j);
                    map.put(node1.getAttribute("property"), node1.getAttribute("column"));
                }
                Element node1 = (Element) node.getElementsByTagName("id").item(0);
                if (node1 != null) {
                    resultMap.setIdPropertyInXml(node1.getAttribute("property"));
                    resultMap.setIdColumnInXml(node1.getAttribute("column"));
                }
                mapper.addResultMap(id, resultMap);
            }
            return mapper;
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Configuration configVerify(Configuration config) {
        PooledDataSource pd = config.getPooledDataSource();
        Environment en = config.getEnvironment();
        AliasRegistry ar = config.getAliasRegistry();

        if (pd == null) {
            throw new RuntimeException("对配置类验证时数据池为空");
        }
        if (en == null) {
            throw new RuntimeException("对配置类验证时环境为空");
        }
        if (en.getId() == null) {
            throw new RuntimeException("对配置类验证时环境的名称为空");
        }
        if (en.getDataSource() == null) {
            throw new RuntimeException("对配置类验证时环境中的数据源为空");
        }
        if (en.getTransactionFactory() == null) {
            throw new RuntimeException("对配置类验证时环境中的事务工厂为空");
        }
        if (ar.getAliasCount() == 0) {
            throw new RuntimeException("对配置类验证时别名为空");
        }
        return config;

    }

    public List<String> convertToPreparedStatementSQL(String sql) {
        List<String> str = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\{([^}]+)}"); //匹配#{。。。}
        Matcher matcher = pattern.matcher(sql);
        int index = 1;
        while (matcher.find()) {
            str.add(matcher.group(1));
        }
        String replacedSql = matcher.replaceAll("?");
        str.addFirst(replacedSql);
        return str;
    }

    // ... existing code ...
    // 类型转换方法，用于处理数据库类型和实体类类型之间的转换
    public Object convertType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        // 数字类型之间的转换
        if (value instanceof Number && isNumericType(targetType)) {
            Number number = (Number) value;
            if (targetType == Integer.class || targetType == int.class) {
                return number.intValue();
            } else if (targetType == Long.class || targetType == long.class) {
                return number.longValue();
            } else if (targetType == Float.class || targetType == float.class) {
                return number.floatValue();
            } else if (targetType == Double.class || targetType == double.class) {
                return number.doubleValue();
            } else if (targetType == Short.class || targetType == short.class) {
                return number.shortValue();
            } else if (targetType == Byte.class || targetType == byte.class) {
                return number.byteValue();
            }
        }

        // String 与数字类型之间的转换
        if (value instanceof String && isNumericType(targetType)) {
            String str = (String) value;
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.valueOf(str);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.valueOf(str);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.valueOf(str);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.valueOf(str);
            } else if (targetType == Short.class || targetType == short.class) {
                return Short.valueOf(str);
            } else if (targetType == Byte.class || targetType == byte.class) {
                return Byte.valueOf(str);
            }
        }

        // 如果无法转换，返回原值
        return value;
    }

    private boolean isNumericType(Class<?> type) {
        return type == Integer.class || type == int.class ||
                type == Long.class || type == long.class ||
                type == Float.class || type == float.class ||
                type == Double.class || type == double.class ||
                type == Short.class || type == short.class ||
                type == Byte.class || type == byte.class;
    }
// ... existing code ...



}

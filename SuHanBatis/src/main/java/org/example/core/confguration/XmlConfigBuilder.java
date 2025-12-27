package org.example.core.confguration;

import org.example.core.transactionFactory.JdbcTransactionFactory;
import org.example.core.transactionFactory.TransactionFactory;

import java.util.Map;
import java.util.Properties;

/// 用于对xml配置文件进行解析的类
/// mapper要不要也在这里呢？
public class XmlConfigBuilder extends BaseBuilder{

    Configuration configuration = super.getConfiguration();

    //在此处将从BaseBuilder处接收到的配置信息进行装配，并返回一个Configuration对象
    //在思考是否将其编写为建造者模式？
    //但是这个并不对外开放，所以我感觉不用写
    public Configuration configBuilder(Map<String , String>  resources){
        PooledDataSource pooledDataSource = new PooledDataSource(resources.get("dataSource0"), resources.get("url0"), resources.get("username0"), resources.get("password0"));
        TransactionFactory transactionFactory = null;
        //其余情况其实我不会搞，也没用过，所以就只处理JDBC
        if("JDBC".equals( resources.get("transactionManager0"))){
             transactionFactory = new JdbcTransactionFactory();
             transactionFactory.setProperties(resources);
        }
        Environment environment = new Environment(resources.get("environment0"),transactionFactory, pooledDataSource);
        configuration.setEnvironment( environment);
        AliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
        for(Map.Entry<String,String> node : resources.entrySet()){
            if(node.getKey().contains("alias")) {
                typeAliasRegistry.registerAliases(node.getValue());
            }
            if(node.getKey().contains("mapper")){
                configuration.addMappers(node.getValue());
            }
        }
        return configuration;
    }

}

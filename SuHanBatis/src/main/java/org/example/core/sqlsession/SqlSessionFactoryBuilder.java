package org.example.core.sqlsession;

import org.example.core.confguration.Configuration;
import org.example.core.confguration.ConfigurationConfigBuilder;
import org.example.core.confguration.XmlConfigBuilder;
import org.example.core.confguration.XmlMapperBuilder;

import java.io.FileInputStream;
import java.util.Map;

public class SqlSessionFactoryBuilder {

    Map<String , String> xmlConfigResources;

    Configuration config;

    XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder();
    ConfigurationConfigBuilder configurationConfigBuilder = new ConfigurationConfigBuilder();
    XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder();

    public SqlSessionFactoryBuilder() {
    }

    public SqlSessionFactory build(FileInputStream xml){
        //获取到xml文件后进行解析，在解析完毕后组合成Configuration对象调用Configuration对应的builder方法
        //想办法写一个能够解析xml文件的类，此时转到XmlConfigBuilder的编写
        //此处的config还是从BaseBUilder处引用过来的
        this.xmlConfigResources = xmlConfigBuilder.getXmlConfigResources(xml);
        this.config = xmlConfigBuilder.configBuilder(xmlConfigResources);
        exchangeMapperResources(this.config);
        return new DefaultSqlSessionFactory(this.config);
    }

    public SqlSessionFactory build(Configuration  config){
        //直接传过来的Configuration对象应该检查资源完整性
        this.config = configurationConfigBuilder.configVerify(config);
        exchangeMapperResources(this.config);
        return new DefaultSqlSessionFactory(this.config);
    }

    private void exchangeMapperResources(Configuration config){
        for(String mapperFile : config.getMapperPathList()){
            config.addMapper(this.xmlMapperBuilder.getXmlMapperResources(mapperFile));
        }
    }
}

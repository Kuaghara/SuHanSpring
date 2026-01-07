package org.example.core.confguration;

import org.example.core.mapper.Mapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class XmlMapperBuilder extends BaseBuilder{

/// 该方法对那些传入的mapper为类名或者为包名进行字符串的转换
    public Mapper getXmlMapperResources(String mapperFile){
        if(mapperFile.contains("file:")){
            mapperFile = mapperFile.substring(5);
        }
        else {

        }
        try {
            return super.getXmlMapperResources(new FileInputStream(mapperFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

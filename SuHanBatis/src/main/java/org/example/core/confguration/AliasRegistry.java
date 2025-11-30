package org.example.core.confguration;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AliasRegistry {
    Map<String, Class<?>> aliasMap = new HashMap<>();
    public void registerAlias(String alias, Class<?> type) {
        aliasMap.put(alias, type);
    }
    public void registerAliases(String packageName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(packageName.replace('.', '/'));
        if(resource != null){
            try {
                File dir = new File(resource.toURI());
                File[] files = dir.listFiles();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

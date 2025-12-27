package org.example.core.confguration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AliasRegistry {
    Map<String, String> aliasMap = new HashMap<>();

    public void registerAlias(String alias ,String packageName) {
            aliasMap.put(alias , packageName);
    }

    //如果传入的是一个包的目录路径，那么会将每个包下的类名作为别名，然后进行注册
    //如果传入的就直接是一个类的包路径，同上然后注册
    public void registerAliases(String packageName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(packageName.replace('.', '/'));

        File file = new File(resource.getFile());
        if(file.isDirectory()){
            for(File f : Objects.requireNonNull(file.listFiles())){
                String fileName = f.getName().replace(".class", "");
                String filePackageName = packageName + "." + fileName;
                registerAlias(fileName, filePackageName);
            }
        }
        else {
            if(file.getName().endsWith(".class")){
                String fileName = file.getName().replace(".class", "");
                registerAlias(fileName, packageName);
            }
        }
    }

    public int getAliasCount() {
        return aliasMap.size();
    }
    public String getPackageName(String alias) {
        return aliasMap.get(alias);
    }
}

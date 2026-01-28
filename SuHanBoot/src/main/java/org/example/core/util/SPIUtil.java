package org.example.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SPIUtil {
    static Map<String, List<String>> spiMap = new HashMap<>();

    public static List<String> getSPIValue(String key) throws IOException {

        if (spiMap.containsKey(key)) {
            return spiMap.get(key);
        }

        List<String> list = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<URL> resource = Collections.list(classLoader.getResources("META-INF/spring.factories"));
        for (URL url : resource) {

            //对情况进行区分，此处有可能为jar包
            if ("jar".equals(url.getProtocol())) {
                JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarConnection.getJarFile();
                JarEntry entry = jarConnection.getJarEntry();

                Properties properties = new Properties();
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    properties.load(inputStream);
                }

                String factoryNames = properties.getProperty(key);
                if (factoryNames != null) {
                    String[] factoryNameArray = factoryNames.split(",");
                    list.addAll(Arrays.asList(factoryNameArray));
                }
            }

            //为file
            else if ("file".equals(url.getProtocol())) {
                Properties properties = new Properties();
                try (InputStream inputStream = url.openStream()) {
                    properties.load(inputStream);
                }
                String factoryNames = properties.getProperty(key);
                if (factoryNames != null) {
                    String[] factoryNameArray = factoryNames.split(",");
                    list.addAll(Arrays.asList(factoryNameArray));
                }
            }
        }
        return list;
    }
}

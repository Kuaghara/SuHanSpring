package org.example.core.httpHandle.argsHandle;

import com.alibaba.fastjson2.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.example.core.annotation.RequestBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;

public class RequestBodyHandle implements HandleArgs {

    @Override
    public boolean support(HttpExchange exchange, Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object handle(HttpExchange exchange, Parameter parameter) {
        try {
            // 从请求体中读取数据
            String requestBody = readRequestBody(exchange);

            if (requestBody.trim().isEmpty()) {
                RequestBody requestBodyAnnotation = parameter.getAnnotation(RequestBody.class);
                if (requestBodyAnnotation.required()) {
                    throw new IllegalArgumentException("必需的请求体数据未提供");
                }
                return null;
            }

            // 使用 Fastjson2 将 JSON 字符串反序列化为对象
            return JSON.parseObject(requestBody, parameter.getType());
        } catch (Exception e) {
            throw new RuntimeException("处理 @RequestBody 注解时出错: " + e.getMessage(), e);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}

package org.example.core.httpHandle;

import com.alibaba.fastjson2.JSON;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ModelHandle {
    public static void sendJson(HttpExchange exchange, ModelAndView modelAndView, int statusCode) {
        try {
            // 设置响应头
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");

            // 将ModelAndView转换为JSON字符串
            String jsonResponse = JSON.toJSONString(modelAndView);
            byte[] jsonBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            // 发送响应头
            exchange.sendResponseHeaders(statusCode, jsonBytes.length);

            // 写入响应体
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonBytes);
                os.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

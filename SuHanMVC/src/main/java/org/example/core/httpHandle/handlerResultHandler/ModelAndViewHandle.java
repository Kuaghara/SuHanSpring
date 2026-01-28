package org.example.core.httpHandle.handlerResultHandler;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.httpHandle.ModelAndView;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class ModelAndViewHandle implements HandlerResultHandler {
    @Override
    public void handle(HttpExchange exchange, Object invoked) {

        System.out.println("进入ModelAndViewHandle");

        try {
            // 设置响应头
            exchange.getResponseHeaders().set("Content-Type", "application/html; charset=utf-8");

            String jsonResponse = "这是一个ModelAndView格式的类，名为：" + ((ModelAndView) invoked).getViewName();
            byte[] jsonBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            // 发送响应头
            exchange.sendResponseHeaders(200, jsonBytes.length);
            // 写入响应体
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonBytes);
                os.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMatch(Object controller, Method method) {
        return true;
    }

}

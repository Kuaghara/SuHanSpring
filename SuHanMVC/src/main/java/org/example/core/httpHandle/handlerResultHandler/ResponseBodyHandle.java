package org.example.core.httpHandle.handlerResultHandler;

import com.alibaba.fastjson2.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.example.core.annotation.ResponseBody;
import org.example.core.util.AnnotationUtil;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ResponseBodyHandle implements HandlerResultHandler {

    @Override
    public void handle(HttpExchange exchange, Object invoked) {

        System.out.println("进入到ResponseBodyHandle");

        try(OutputStream responseBody = exchange.getResponseBody()){
            String json = JSON.toJSONString(invoked);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            responseBody.write(json.getBytes());
            responseBody.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isMatch(Object controller, Method method) {
        System.out.println("进入到ResponseBodyHandle isMatch");

        Class<?> clazz = controller.getClass();
        List<Annotation> annonationsList = AnnotationUtil.getAnnotationsList(clazz,new ArrayList<>());
        if (AnnotationUtil.listIncludeAnnotation(annonationsList, ResponseBody.class)) {
            return true;
        }
        return method.isAnnotationPresent(ResponseBody.class);
    }

}

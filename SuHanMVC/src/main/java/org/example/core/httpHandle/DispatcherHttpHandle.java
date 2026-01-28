package org.example.core.httpHandle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.core.context.beanFactory.BeanFactory;
import org.example.core.httpHandle.argsHandle.AnnotationHandle;
import org.example.core.httpHandle.handlerResultHandler.HandleResult;
import org.example.core.httpHandle.interceptor.InterceptorRegistration;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DispatcherHttpHandle implements HttpHandler {
    private final InterceptorRegistration interceptorRegistration = new InterceptorRegistration();
    Map<RouteKey, HandlerMethod> routes = new HashMap<>();
    Exception e;
    private BeanFactory beanFactory;

    public DispatcherHttpHandle() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {


        System.out.println();
        System.out.println();
        System.out.println(exchange + "已有访问进入到进入到dispactcher中");

        init(exchange);

        HandlerMethod handlerMethod = matchingRoutes(exchange);
        String requestPath = exchange.getRequestURI().getPath();

        Object controller;
        try {
            controller = beanFactory.getBean(handlerMethod.getControllerName());
            System.out.println("得到controller" + controller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(exchange + "进入到拦截器前");
        System.out.println("输出测试。。。");

        interceptorRegistration.preHandle(requestPath, exchange, controller);

        System.out.println(exchange + "完成拦截器前的调用");
        //拦截器成功获取后需要获取到该方法的参数，并且还要对相关参数的注解进行解析

        try {
            System.out.println("开始方法调用");
            Method method = handlerMethod.getMethod();
            Parameter[] parameters = method.getParameters();
            System.out.println("参数获取开始");
            Object[] args = getArgs(exchange, parameters);
            System.out.println("参数获取成功" + Arrays.toString(args));
            Object invoked = method.invoke(controller, args);
            System.out.println("方法调用成功" + invoked);
            System.out.println("开始数据返回检查");


            System.out.println("开始返回数据");

            new HandleResult().handleResult(exchange, invoked, controller, method);

            interceptorRegistration.PostHandle(requestPath, exchange, controller, null);

        } catch (Exception e) {
            this.e = e;
            System.out.println("在DispatcherHttpHandle处出现异常");
        } finally {
            interceptorRegistration.AfterCompletion(requestPath, exchange, controller, e);
        }
    }


    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void addRoute(RouteKey routeKey, HandlerMethod handlerMethod) {
        routes.put(routeKey, handlerMethod);
    }

    public void addRoutes(Map<RouteKey, HandlerMethod> routes) {
        this.routes.putAll(routes);
    }

    private Object[] getArgs(HttpExchange exchange, Parameter[] parameters) {
        Object[] args = new Object[parameters.length];
        int i = 0;

        AnnotationHandle annotationHandle = new AnnotationHandle();

        for (Parameter parameter : parameters) {
            Object arg = annotationHandle.handleParameter(exchange, parameter);
            if (arg != null) {
                args[i++] = arg;
            }
        }

        return args;
    }

    private HandlerMethod fuzzyMatching(HandlerMethod handlerMethod, String requestMethod, String requestPath) {
        // 如果没有找到精确匹配，尝试匹配 /** 路径
        if (handlerMethod == null) {
            for (Map.Entry<RouteKey, HandlerMethod> entry : routes.entrySet()) {
                RouteKey key = entry.getKey();
                if ("/**".equals(key.getRequestPath())) {
                    return entry.getValue();
                }
            }
        }

        // 如果仍然没有找到，尝试更灵活的匹配
        if (handlerMethod == null) {
            for (Map.Entry<RouteKey, HandlerMethod> entry : routes.entrySet()) {
                RouteKey key = entry.getKey();
                if (key.getRequestMethod().equals(requestMethod)) {
                    String patternPath = key.getRequestPath();

                    // 检查是否为 /** 模式
                    if ("/**".equals(patternPath)) {
                        return entry.getValue();
                    }

                    // 检查是否为 /path/** 模式
                    if (patternPath.endsWith("/**")) {
                        String basePath = patternPath.substring(0, patternPath.length() - 3);
                        if (requestPath.startsWith(basePath)) {
                            return entry.getValue();
                        }
                    }
                }
            }
        }
        return handlerMethod;
    }

    private void init(HttpExchange exchange) {
        // 添加 CORS 头部
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");

        // 处理 OPTIONS 预检请求
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "*");
            try {
                exchange.sendResponseHeaders(200, -1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private HandlerMethod matchingRoutes(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();

        RouteKey routeKey = new RouteKey(requestMethod, requestPath);
        HandlerMethod handlerMethod = routes.get(routeKey);
        handlerMethod = fuzzyMatching(handlerMethod, requestMethod, requestPath);

        for (Map.Entry<RouteKey, HandlerMethod> entry : routes.entrySet()) {
            System.out.println("遍历路由表中：" + entry.getKey() + " key");
            System.out.println("遍历路由表中" + entry.getValue() + " value");
        }

        System.out.println("进入的请求方法" + requestMethod + " 请求路径：" + requestPath);

        System.out.println("获取到的controller名：" + handlerMethod.getControllerName() + "方法名" + handlerMethod.getMethod());

        return handlerMethod;
    }


}

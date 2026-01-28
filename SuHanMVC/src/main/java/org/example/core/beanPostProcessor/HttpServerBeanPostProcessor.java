package org.example.core.beanPostProcessor;

import com.sun.net.httpserver.HttpServer;
import org.example.core.annotation.*;
import org.example.core.context.ThreadPoolManager;
import org.example.core.context.beanFactory.BeanDefinitionRegistry;
import org.example.core.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.core.httpHandle.DispatcherHttpHandle;
import org.example.core.httpHandle.HandlerMethod;
import org.example.core.httpHandle.RouteKey;
import org.example.core.httpHandle.favicon;
import org.example.core.httpHandle.interceptor.InterceptorRegistration;
import org.example.core.informationEntity.AnnotatedGenericBeanDefinition;
import org.example.core.informationEntity.BeanDefinition;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServerBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    Map<RouteKey, HandlerMethod> routes = new HashMap<>();
    private HttpServer httpServer;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        DispatcherHttpHandle dispatcherHttpHandle = (DispatcherHttpHandle) beanFactory.getBean("DispatcherHttpHandle");
        dispatcherHttpHandle.setBeanFactory(beanFactory);
        dispatcherHttpHandle.addRoutes(routes);

        // Avoid re-registering contexts on refresh or repeated post-processing.
        if (!beanFactory.containsBean("HttpServer")) {
            try {
                httpServer = httpServer == null ? HttpServer.create(new InetSocketAddress(8080), 0) : httpServer;
                httpServer.setExecutor(ThreadPoolManager.getThreadPool());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            httpServer.createContext("/", dispatcherHttpHandle);
            httpServer.createContext("/favicon.ico", new favicon());
            beanFactory.registerSingleton("HttpServer", httpServer);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        //给下面的DispatcherHttpHandle自动注入用
        BeanDefinition InterceptorRegistration = new AnnotatedGenericBeanDefinition(InterceptorRegistration.class);
        registry.registerBeanDefinition("InterceptorRegistration", InterceptorRegistration);

        //注册DispatcherHttpHandle给上面的方法用
        BeanDefinition dHHBD = new AnnotatedGenericBeanDefinition(DispatcherHttpHandle.class);
        registry.registerBeanDefinition("DispatcherHttpHandle", dHHBD);

        //这里进行扫描
        //把RequsetMapping注解的信息和类联系起来
        Map<String, BeanDefinition> beanDefinitionMap = registry.getBeanDefinitionMap();
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {

            List<Annotation> allAnnotation = beanDefinition.getAllAnnotation();

            for (Annotation annotation : allAnnotation) {

                if (annotation.annotationType().equals(Controller.class)) {
                    Class<?> clazz = beanDefinition.getClazz();

                    //获取类上的RequestMapping注解
                    String prePath = "";
                    RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);
                    if (classRequestMapping != null) {
                        prePath = classRequestMapping.path()[0];
                    }

                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        //在拼接的时候把类的路径拼进去
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            HandlerMethod handlerMethod = new HandlerMethod(beanDefinition.getClassName(), method);

                            //先拼下路径
                            String[] path = splicingPaths(prePath, requestMapping.path());

                            //再看是不是多路径
                            multipathProcessing(path, requestMapping, handlerMethod);
                        }

                        //接下来两个同理
                        if (method.isAnnotationPresent(PostMapping.class)) {
                            PostMapping postMapping = method.getAnnotation(PostMapping.class);
                            HandlerMethod handlerMethod = new HandlerMethod(beanDefinition.getClassName(), method);

                            String[] path = splicingPaths(prePath, postMapping.path());
                            multipathProcessing(path, postMapping, handlerMethod);
                        }

                        if (method.isAnnotationPresent(GetMapping.class)) {
                            GetMapping getMapping = method.getAnnotation(GetMapping.class);
                            HandlerMethod handlerMethod = new HandlerMethod(beanDefinition.getClassName(), method);

                            String[] path = splicingPaths(prePath, getMapping.path());
                            multipathProcessing(path, getMapping, handlerMethod);
                        }
                    }
                }
            }
        }

    }

    private String[] splicingPaths(String prePath, String[] paths) {
        for (int i = 0; i < paths.length; i++) {
            paths[i] = prePath + paths[i];
        }
        return paths;
    }

    private <T extends Annotation> void multipathProcessing(String[] paths, T mappingAnnotation, HandlerMethod handlerMethod) {
        String requestMethod = "";
        if (mappingAnnotation instanceof RequestMapping) {
            if (((RequestMapping) mappingAnnotation).method().equals(RequestMethod.GET)) {
                requestMethod = "GET";
            } else {
                requestMethod = "POST";
            }
        } else if (mappingAnnotation instanceof PostMapping) {
            requestMethod = "POST";
        } else if (mappingAnnotation instanceof GetMapping) {
            requestMethod = "GET";
        }

        if (requestMethod.isEmpty()) {
            throw new RuntimeException("没有找到对应的请求方式");
        }


        if (paths.length > 1) {
            for (String path : paths) {
                RouteKey routeKey = new RouteKey(requestMethod, path);
                routes.put(routeKey, handlerMethod);
            }
        } else {
            RouteKey routeKey = new RouteKey(requestMethod, paths[0]);
            routes.put(routeKey, handlerMethod);
        }
    }
}

package org.example.core.httpHandle.interceptor;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.httpHandle.ModelAndView;

import java.util.*;

public class InterceptorRegistration {
    List<InterceptorRegistry> interceptors = new ArrayList<>();

    Deque<InterceptorRegistry> preHandleInterceptors = new ArrayDeque<>();
    Deque<InterceptorRegistry> AfterHandleInterceptors = new ArrayDeque<>();

    public InterceptorRegistry addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistry interceptorRegistry = new InterceptorRegistry(interceptor);
        interceptors.add(interceptorRegistry);
        return interceptorRegistry;
    }


    //关于此处应有的递归逻辑，我打算先编写完，能够让他跑起来再去换成递归
    public void preHandle(String requestPath, HttpExchange exchange, Object handler) {
        System.out.println("=== 开始执行拦截器 ===");
        System.out.println("请求路径: " + requestPath);
        System.out.println("拦截器数量: " + interceptors.size());

        try {
            for (InterceptorRegistry interceptor : interceptors) {
                System.out.println("对拦截器进行循环遍历：" + interceptor);
            }

            for (InterceptorRegistry interceptor : interceptors) {
                List<String> pathPatterns = interceptor.pathPatterns;
                List<String> excludePathPatterns = interceptor.excludePathPatterns;
                // 将访问路径按"/"分割（使用Stream）
                List<String> pathList = Arrays.stream(requestPath.split("/"))
                        .filter(part -> !part.isEmpty())
                        .toList();
                //接下来遍历访问路径，并将访问路径进行分割
                for (String pathPattern : pathPatterns) {
                    List<String> pathPatternList = Arrays.stream(pathPattern.split("/"))
                            .filter(part -> !part.isEmpty())
                            .toList();
                    //匹配成功，允许进行拦截
                    if (matchPath(pathList, pathPatternList, excludePathPatterns, 0)) {
                        preHandleInterceptors.push(interceptor);
                    }
                }
            }

            System.out.println("匹配到的拦截器数量: " + preHandleInterceptors.size());

            for (InterceptorRegistry interceptor : preHandleInterceptors) {
                System.out.println("执行拦截器: " + interceptor);
                boolean b = interceptor.doPreHandle(exchange, handler);
                if (!b) {
                    System.out.println("拦截器返回false，中断执行");
                    return;
                }
                AfterHandleInterceptors.push(interceptor);
            }

            System.out.println("拦截器执行完成");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("拦截器处理出错: " + e.getMessage());
        }
    }

    public void PostHandle(String requestPath, HttpExchange exchange, Object handler, ModelAndView modelAndView) {
        for (InterceptorRegistry interceptor : AfterHandleInterceptors) {
            interceptor.doPostHandle(exchange, handler, modelAndView);
        }
    }

    public void AfterCompletion(String requestPath, HttpExchange exchange, Object handler, Exception ex) {
        for (InterceptorRegistry interceptor : AfterHandleInterceptors) {
            interceptor.doAfterCompletion(exchange, handler, ex);
        }
    }

    private boolean matchPath(List<String> pathList, List<String> pathPatternList, List<String> excludePatternsList, int index) {
        if (index >= pathList.size()) {
            return false;
        }
        if (excludePatternsList.get(index).equals("**")) {
            return false;
        }
        //当拦截器配置了*时，假如访问路径长度大于配置路径长度，则继续拦截
        //如果就到这了，那就不拦截了
        if (excludePatternsList.get(index).equals("*")) {
            if (pathList.size() > excludePatternsList.size()) {
                return true;
            }
            return false;
        }

        if (pathPatternList.get(index).equals("**")) {
            return true;
        }

        //是最后一个，并且长度相同，并且路径完全相同，那就不拦截
        if (pathList.size() == excludePatternsList.size() && index == pathList.size() - 1 && Objects.equals(pathList.get(index), excludePatternsList.get(index))) {
            return false;
        }
        if (pathPatternList.get(index).equals("*") || pathPatternList.get(index).equals(pathList.get(index))) {
            return matchPath(pathList, pathPatternList, excludePatternsList, index + 1);
        }
        return false;
    }

}

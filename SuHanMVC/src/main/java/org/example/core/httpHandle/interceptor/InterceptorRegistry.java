package org.example.core.httpHandle.interceptor;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.httpHandle.ModelAndView;

import java.lang.reflect.Method;
import java.util.List;

public class InterceptorRegistry {
    HandlerInterceptor interceptor;

    List<String> pathPatterns;
    List<String> excludePathPatterns;

    public InterceptorRegistry(HandlerInterceptor interceptor){
        this.interceptor = interceptor;
    }


    public InterceptorRegistry addPathPatterns(String pathPattern){
            this.pathPatterns.add(pathPattern);
            return this;
    }
    public InterceptorRegistry excludePathPatterns(String pathPattern){
            this.excludePathPatterns.add(pathPattern);
            return this;
    }

    public void doHandle(HttpExchange  exchange, Object handler, ModelAndView modelAndView, Exception ex, Method method){
        try {
            interceptor.preHandle(exchange, handler);

            interceptor.postHandle(exchange, handler, modelAndView);
            interceptor.afterCompletion(exchange, handler, ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean doPreHandle(HttpExchange  exchange, Object handler) {
        try {
            boolean next = interceptor.preHandle(exchange, handler);
            if(next){
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public void doPostHandle(HttpExchange  exchange, Object handler, ModelAndView modelAndView) {
        try {
            interceptor.postHandle(exchange, handler,modelAndView );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void doAfterCompletion(HttpExchange  exchange, Object handler, Exception ex) {
        try {
            interceptor.afterCompletion(exchange, handler, ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

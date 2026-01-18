package org.example.core.httpHandle.interceptor;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.httpHandle.ModelAndView;

public interface HandlerInterceptor {
    default boolean preHandle(HttpExchange exchange, Object handler) throws Exception {
        return true;
    }

    default void postHandle(HttpExchange exchange, Object handler, ModelAndView modelAndView) throws Exception {
    }

    default void afterCompletion(HttpExchange exchange, Object handler,Exception ex) throws Exception {
    }
}

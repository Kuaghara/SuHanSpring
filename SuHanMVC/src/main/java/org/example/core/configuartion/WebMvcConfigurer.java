package org.example.core.configuartion;

import org.example.core.httpHandle.interceptor.InterceptorRegistry;

public interface WebMvcConfigurer {
    default void addInterceptors(InterceptorRegistry registry) {
    }

    ;
}

// C:\Users\Y2719\Desktop\code\java\SuHan-main\SuHanBoot\src\main\java\org\example\core\autoconfigure\WebMvcAutoConfiguration.java
package org.example.core.autoconfigure;

import com.sun.net.httpserver.HttpServer;
import org.example.core.annotation.*;
import org.example.core.configuartion.WebMvcConfigurer;
import org.example.core.context.ThreadPoolManager;
import org.example.core.context.beanFactory.BeanFactory;
import org.example.core.httpHandle.DispatcherHttpHandle;

import java.net.InetSocketAddress;

@Configuration
@ConditionalOnClass(name = "org.example.core.httpHandle.DispatcherHttpHandle")  // 当类路径中有DispatcherHttpHandle类时才生效
@ConditionalOnMissingBean(HttpServer.class)  // 当容器中没有HttpServer Bean时才创建
@EnableWebMvc
public class WebMvcAutoConfiguration implements WebMvcConfigurer {


    @Bean
    @ConditionalOnMissingBean  // 当没有用户自定义的HttpServer时才创建默认的
    public HttpServer httpServer(DispatcherHttpHandle dispatcherHttpHandle) throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.setExecutor(ThreadPoolManager.getThreadPool());
        httpServer.createContext("/", dispatcherHttpHandle);
        return httpServer;
    }

    @Bean
    @ConditionalOnMissingBean  // 当没有用户自定义的DispatcherHttpHandle时才创建
    public DispatcherHttpHandle dispatcherHttpHandle() {
        // 创建DispatcherHttpHandle实例
        return new DispatcherHttpHandle();
    }

    // 可以添加更多的Web MVC相关的Bean配置
    // 例如视图解析器、拦截器、转换器等
}

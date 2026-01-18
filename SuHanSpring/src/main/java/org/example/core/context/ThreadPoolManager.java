package org.example.core.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private static final ExecutorService executorService = Executors.newCachedThreadPool(r->{
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static ExecutorService getThreadPool() {
        return executorService;
    }

    public static boolean isAvailable() {
        return !executorService.isShutdown() && !executorService.isTerminated();
    }

    public static synchronized void shutdown() {
        if(isAvailable()){
            executorService.shutdown();
        }
    }

    // 添加 JVM 关闭钩子作为备用保障
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadPoolManager::shutdown));
    }
}


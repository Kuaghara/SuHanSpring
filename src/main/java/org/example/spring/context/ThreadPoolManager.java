package org.example.spring.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static volatile boolean isShuttingDown = false;

    public static ExecutorService getThreadPool() {
        return executorService;
    }

    public static boolean isAvailable() {
        return !executorService.isShutdown() && !executorService.isTerminated() && !isShuttingDown;
    }

    public static synchronized void shutdown() {
        if (!isShuttingDown && !executorService.isShutdown()) {
            isShuttingDown = true;
            executorService.shutdown();
            try {
                // 等待最多10秒让现有任务完成
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // 添加 JVM 关闭钩子作为备用保障
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadPoolManager::shutdown));
    }
}


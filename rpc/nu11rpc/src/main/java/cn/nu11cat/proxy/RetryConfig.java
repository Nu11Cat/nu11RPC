package cn.nu11cat.proxy;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class RetryConfig {
    // 最大重试次数（默认3次）
    private final int maxAttempts;
    // 重试间隔（毫秒，默认500ms）
    private final long backoffMillis;
    // 需要重试的异常类型（默认只重试IO和超时）
    private final Class<? extends Exception>[] retryableExceptions;

    public RetryConfig(int maxAttempts, long backoffMillis,
                       Class<? extends Exception>... retryableExceptions) {
        this.maxAttempts = maxAttempts;
        this.backoffMillis = backoffMillis;
        this.retryableExceptions = retryableExceptions;
    }

    // 默认配置（可直接使用）
    public static RetryConfig defaultConfig() {
        return new RetryConfig(
                3,
                500,
                IOException.class,
                SocketTimeoutException.class
        );
    }

    // 判断异常是否可重试
    public boolean shouldRetry(Exception e) {
        for (Class<? extends Exception> ex : retryableExceptions) {
            if (ex.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public int getMaxAttempts() { return maxAttempts; }
    public long getBackoffMillis() { return backoffMillis; }
}
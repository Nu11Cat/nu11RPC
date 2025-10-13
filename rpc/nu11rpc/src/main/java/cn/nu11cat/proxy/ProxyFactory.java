package cn.nu11cat.proxy;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.common.URL;
import cn.nu11cat.loadbalance.LoadBalance;
import cn.nu11cat.loadbalance.RandomLoadBalance;
import cn.nu11cat.protocol.HttpClient;
import cn.nu11cat.register.MapRemoteRegister;
import com.alibaba.nacos.api.exception.NacosException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProxyFactory {

    public static <T> T getProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new RpcInvocationHandler(interfaceClass)
        );
    }

    private static class RpcInvocationHandler implements InvocationHandler {
        private final Class<?> interfaceClass;
        private final HttpClient httpClient = new HttpClient();

        public RpcInvocationHandler(Class<?> interfaceClass) {
            this.interfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 1. Mock数据支持
            String mock = System.getProperty("mock");
            if (mock != null && mock.startsWith("return:")) {
                return mock.replace("return:", "");
            }

            // 2. 构造调用信息
            Invocation invocation = new Invocation(
                    interfaceClass.getName(),
                    method.getName(),
                    method.getParameterTypes(),
                    args
            );

            // 3. 服务调用
            return handleServiceCall(invocation);
        }

        private String handleServiceCall(Invocation invocation) {
            try {
                List<URL> availableUrls = getAvailableUrls(invocation.getInterfaceName());
                return doRetry(invocation, availableUrls);
            } catch (NacosException e) {
                return "Fallback: Nacos错误 - " + e.getErrMsg();
            }
        }

        private List<URL> getAvailableUrls(String serviceName) throws NacosException {
            List<URL> urls = MapRemoteRegister.NacosRemoteRegister.get(serviceName);
            if (urls == null || urls.isEmpty()) {
                throw new NacosException(NacosException.INVALID_PARAM, "服务实例列表为空");
            }
            return urls;
        }

        private String doRetry(Invocation invocation, List<URL> availableUrls) {
            RetryConfig retryConfig = RetryConfig.defaultConfig(); // 使用默认配置
            int attempt = 0;
            List<URL> invokedUrls = new ArrayList<>();
            Exception lastException = null;

            while (attempt <= retryConfig.getMaxAttempts()) {
                URL selectedUrl = selectAvailableUrl(availableUrls, invokedUrls);
                if (selectedUrl == null) break;

                try {
                    return httpClient.send(selectedUrl.getHostname(), selectedUrl.getPort(), invocation);
                } catch (Exception e) {
                    lastException = e;
                    invokedUrls.add(selectedUrl);
                    System.err.printf("调用 %s 失败（尝试 %d/%d）: %s\n",
                            selectedUrl,
                            attempt + 1,
                            retryConfig.getMaxAttempts(),
                            e.getMessage()
                    );

                    // 判断是否应该重试
                    if (!retryConfig.shouldRetry(e)) {
                        break;
                    }

                    // 执行退避等待
                    if (attempt < retryConfig.getMaxAttempts()) {
                        try {
                            Thread.sleep(retryConfig.getBackoffMillis());
                        } catch (InterruptedException ignored) {}
                    }
                    attempt++;
                }
            }

            return buildFallbackResponse(lastException);
        }

        private URL selectAvailableUrl(List<URL> availableUrls, List<URL> invokedUrls) {
            List<URL> candidates = new ArrayList<>(availableUrls);
            candidates.removeAll(invokedUrls);

            // 使用随机策略
            LoadBalance loadBalance = new RandomLoadBalance();
            return candidates.isEmpty() ? null : loadBalance.select(candidates);
        }

        private String buildFallbackResponse(Exception lastException) {
            String errorMsg = Objects.nonNull(lastException) ?
                    lastException.getMessage() : "无可用服务实例";
            return "Fallback: " + errorMsg;
        }
    }
}
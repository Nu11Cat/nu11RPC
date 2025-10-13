package cn.nu11cat.loadbalance;

import cn.nu11cat.common.URL;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//轮询策略
public class RoundRobinLoadBalance implements LoadBalance {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public URL select(List<URL> urls) {
        if (urls.isEmpty()) throw new IllegalStateException("无可用服务");
        return urls.get(Math.abs(counter.getAndIncrement() % urls.size()));
    }
}
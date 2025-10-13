package cn.nu11cat.loadbalance;

import cn.nu11cat.common.URL;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//加权轮询：服务注册时指定权重
public class WeightedRoundRobinLoadBalance implements LoadBalance {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public URL select(List<URL> urls) {
        if (urls.isEmpty()) throw new IllegalStateException("无可用服务");

        // 计算总权重
        int totalWeight = urls.stream().mapToInt(URL::getWeight).sum();
        int currentIndex = counter.getAndIncrement() % totalWeight;

        // 按权重选择
        for (URL url : urls) {
            currentIndex -= url.getWeight();
            if (currentIndex < 0) {
                return url;
            }
        }
        return urls.get(0);
    }
}


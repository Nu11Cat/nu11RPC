package cn.nu11cat.loadbalance;

import cn.nu11cat.common.URL;

import java.util.List;
import java.util.Random;
 //随机策略（默认）
public class RandomLoadBalance implements LoadBalance {
    private final Random random = new Random();

    @Override
    public URL select(List<URL> urls) {
        if (urls.isEmpty()) throw new IllegalStateException("无可用服务");
        return urls.get(random.nextInt(urls.size()));
    }
}

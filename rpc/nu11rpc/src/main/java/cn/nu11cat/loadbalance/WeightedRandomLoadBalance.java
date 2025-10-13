package cn.nu11cat.loadbalance;

import cn.nu11cat.common.URL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
//加权随机
public class WeightedRandomLoadBalance implements LoadBalance {
    @Override
    public URL select(List<URL> urls) {
        int totalWeight = urls.stream().mapToInt(URL::getWeight).sum();
        int randomWeight = ThreadLocalRandom.current().nextInt(totalWeight);

        for (URL url : urls) {
            randomWeight -= url.getWeight();
            if (randomWeight < 0) {
                return url;
            }
        }
        return urls.get(0);
    }
}

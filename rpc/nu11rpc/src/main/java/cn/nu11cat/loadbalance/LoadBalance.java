package cn.nu11cat.loadbalance;

import cn.nu11cat.common.URL;

import java.util.List;
import java.util.Random;

public interface LoadBalance {
    /**
     * 从可用服务列表中选择一个
     * @param urls 可用服务列表
     * @return 选中的服务节点
     */
    URL select(List<URL> urls);
}
package cn.nu11cat.loadbalance;

import cn.nu11cat.common.URL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
//一致性哈希
public class ConsistentHashLoadBalance implements LoadBalance {
    private final TreeMap<Long, URL> virtualNodes = new TreeMap<>();
    private final int replicaNumber = 160; // 虚拟节点数

    @Override
    public URL select(List<URL> urls) {
        if (urls.isEmpty()) throw new IllegalStateException("无可用服务");

        // 构建哈希环
        for (URL url : urls) {
            for (int i = 0; i < replicaNumber; i++) {
                byte[] digest = md5(url.toString() + i);
                for (int h = 0; h < 4; h++) {
                    long hash = hash(digest, h);
                    virtualNodes.put(hash, url);
                }
            }
        }

        // 根据请求参数哈希选择（示例用随机值）
        long hash = hash(md5(UUID.randomUUID().toString()), 0);
        SortedMap<Long, URL> tail = virtualNodes.tailMap(hash);
        return tail.isEmpty() ? virtualNodes.firstEntry().getValue() : tail.get(tail.firstKey());
    }

    private byte[] md5(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes(StandardCharsets.UTF_8));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
    private long hash(byte[] digest, int number) {
        // 每4字节生成一个哈希值（MD5共16字节，可生成4个哈希值）
        return ((long) (digest[3 + number * 4] & 0xFF) << 24) |
                ((long) (digest[2 + number * 4] & 0xFF) << 16) |
                ((long) (digest[1 + number * 4] & 0xFF) << 8) |
                ((long) (digest[number * 4] & 0xFF));
    }
}
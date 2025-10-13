package cn.nu11cat.register;

import cn.nu11cat.common.URL;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapRemoteRegister {

    private static Map<String, List<URL>> map = new HashMap<>();

    public static void register(String interfaceName, URL url) {
        List<URL> list = map.get(interfaceName);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(url);

        map.put(interfaceName, list);

        saveFile();
    }

    public static List<URL> get(String interfaceName)
    {
        map = getFile();
        return map.get(interfaceName);
    }

    private static void saveFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/temp.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, List<URL>> getFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream("/temp.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Map<String, List<URL>>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public class NacosRemoteRegister {
        private static final NamingService namingService;
        private static final String NACOS_ADDR = "127.0.0.1:8848"; // Nacos地址

        static {
            try {
                namingService = NamingFactory.createNamingService(NACOS_ADDR);
            } catch (NacosException e) {
                throw new RuntimeException("Nacos初始化失败", e);
            }
        }

        // 服务注册
        public static void register(String serviceName, URL url) throws NacosException {
            namingService.registerInstance(
                    serviceName,
                    url.getHostname(),
                    url.getPort()
            );
        }

        // 服务发现
        public static List<URL> get(String serviceName) throws NacosException {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            return instances.stream()
                    .map(instance -> new URL(instance.getIp(), instance.getPort()))
                    .collect(Collectors.toList());
        }
    }

}

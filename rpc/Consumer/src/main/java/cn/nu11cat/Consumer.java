package cn.nu11cat;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.protocol.HttpClient;
import cn.nu11cat.proxy.ProxyFactory;

public class Consumer {
    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("nu11cat1");
        System.out.println(result);

    }
}

package cn.nu11cat;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.protocol.HttpClient;
import cn.nu11cat.proxy.ProxyFactory;

public class Consumer {
    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("nu11cat2");
        System.out.println(result);

    }
}


//package cn.nu11cat;
//
//public class Consumer {
//    public static void main(String[] args) {
//        // 本地直接 new，不走代理、不走网络
//        HelloService helloService = new HelloServiceImpl();
//
//        String result = helloService.sayHello("nu11cat2");
//        System.out.println(result);
//    }
//}

package cn.nu11cat;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.protocol.HttpClient;

public class Consumer {
    public static void main(String[] args) {
//        HelloService helloService = ?;
//        String result = helloService.sayHello("world");
//        System.out.println(result);

        Invocation invocation = new Invocation(HelloService.class.getName(), "sayHello", new Class[]{String.class}, new Object[]{"nu11cat"});

        HttpClient httpClient = new HttpClient();
        String result = httpClient.send("localhost", 8080, invocation);
        System.out.println(result);

    }
}

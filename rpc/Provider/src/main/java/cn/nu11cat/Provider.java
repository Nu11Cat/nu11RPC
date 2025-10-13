package cn.nu11cat;

import cn.nu11cat.protocol.HttpServer;
import cn.nu11cat.register.LocalRegister;

public class Provider {

    public static void main(String[] args) {

        LocalRegister.register(HelloService.class.getName(), HelloService.class);

        HttpServer server = new HttpServer();
        server.start("localhost", 8080);
    }
}

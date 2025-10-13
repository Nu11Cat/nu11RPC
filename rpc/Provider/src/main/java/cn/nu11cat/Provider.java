package cn.nu11cat;

import cn.nu11cat.protocol.HttpServer;
import cn.nu11cat.register.LocalRegister;

public class Provider {

    public static void main(String[] args) {

        LocalRegister.register(HelloService.class.getName(), "1.0", HelloServiceImpl.class);

        HttpServer server = new HttpServer();
        server.start("localhost", 8080);
    }
}

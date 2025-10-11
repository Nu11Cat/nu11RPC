package cn.nu11cat;

import cn.nu11cat.protocol.HttpServer;

public class Provider {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start("localhost", 8080);
    }
}

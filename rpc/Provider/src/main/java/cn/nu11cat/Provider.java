package cn.nu11cat;

import cn.nu11cat.common.URL;
import cn.nu11cat.protocol.HttpServer;
import cn.nu11cat.register.LocalRegister;
import cn.nu11cat.register.MapRemoteRegister;

public class Provider {

    public static void main(String[] args) {
        //本地注册
        LocalRegister.register(HelloService.class.getName(), "1.0", HelloServiceImpl.class);
        //注册中心注册
        URL url = new URL("localhost", 8080);
        MapRemoteRegister.register(HelloService.class.getName(), url);

        HttpServer server = new HttpServer();
        server.start(url.getHostname(), url.getPort());
    }
}

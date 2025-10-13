package cn.nu11cat.protocol;

import cn.nu11cat.common.URL;
import cn.nu11cat.register.MapRemoteRegister;
import com.alibaba.nacos.api.exception.NacosException;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

public class HttpServer {

    public void start(String hostname, Integer port) {
        try {
            // 读取配置（示例：server.name=netty）
            Tomcat tomcat = new Tomcat();

            // 获取 Server、Service
            Server server = tomcat.getServer();
            Service service = server.findService("Tomcat");

            // 创建 Connector（端口）
            Connector connector = new Connector();
            connector.setPort(port);

            // 创建 Engine（引擎）
            Engine engine = new StandardEngine();
            engine.setDefaultHost(hostname);

            // 创建 Host（主机）
            Host host = new StandardHost();
            host.setName(hostname);

            // 创建 Context（上下文）
            String contextPath = "";
            Context context = new StandardContext();
            context.setPath(contextPath);
            context.addLifecycleListener(new Tomcat.FixContextListener());

            // 组装结构：engine → host → context
            host.addChild(context);
            engine.addChild(host);

            // 将 Engine 与 Connector 绑定到 Service
            service.setContainer(engine);
            service.addConnector(connector);

            // 注册 Servlet
            tomcat.addServlet(contextPath, "dispatcher", new DispatcherServlet());
            context.addServletMappingDecoded("/*", "dispatcher");

            // 2. 注册服务到Nacos
            String serviceName = "cn.nu11cat.HelloService"; // 与服务接口名一致
            URL url = new URL(hostname, port);
            MapRemoteRegister.NacosRemoteRegister.register(serviceName, url);

            System.out.println("[成功] 服务注册: " + serviceName + " -> " + url);

            // 3. 启动Tomcat
            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            System.err.println("[失败] 服务启动异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

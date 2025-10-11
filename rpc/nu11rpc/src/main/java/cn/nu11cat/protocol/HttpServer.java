package cn.nu11cat.protocol;

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

//            // 注册 Servlet
//            tomcat.addServlet(contextPath, "dispatcher", new DispatcherServlet());
//            context.addServletMappingDecoded("/*", "dispatcher");

            // 启动 Tomcat
            tomcat.start();
            tomcat.getServer().await();

        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

}

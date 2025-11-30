package cn.nu11cat.protocol;

import cn.nu11cat.common.URL;
import cn.nu11cat.register.MapRemoteRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private final String hostname;
    private final int port;

    public NettyServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RpcDecoder()); // JSON -> Invocation
                            pipeline.addLast(new RpcEncoder()); // Object -> JSON
                            pipeline.addLast(new NettyServerHandler()); // 调用本地服务
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync();

            // 注册服务
            String serviceName = "cn.nu11cat.HelloService";
            MapRemoteRegister.NacosRemoteRegister.register(serviceName, new URL(hostname, port));
            System.out.println("[NettyServer] 服务注册: " + serviceName + " -> " + hostname + ":" + port);

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

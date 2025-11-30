package cn.nu11cat.protocol;

import cn.nu11cat.common.Invocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

public class NettyClient {

    private static final EventLoopGroup GROUP = new NioEventLoopGroup();

    public Object send(String hostname, int port, Invocation invocation) throws Exception {

        CompletableFuture<Object> resultFuture = new CompletableFuture<>();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(GROUP)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(new SimpleChannelInboundHandler<Object>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
                                resultFuture.complete(msg);
                            }
                        });
                    }
                });

        ChannelFuture future = bootstrap.connect(hostname, port).sync();
        future.channel().writeAndFlush(invocation);

        return resultFuture.get();  // 阻塞等待结果
    }
}

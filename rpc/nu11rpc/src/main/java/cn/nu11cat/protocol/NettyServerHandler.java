package cn.nu11cat.protocol;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.register.LocalRegister;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class NettyServerHandler extends SimpleChannelInboundHandler<Invocation> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Invocation invocation) throws Exception {
        Class<?> implClass = LocalRegister.get(invocation.getInterfaceName(), "1.0");
        Object result;
        if (implClass != null) {
            Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            result = method.invoke(implClass.getDeclaredConstructor().newInstance(), invocation.getParameters());
        } else {
            result = "service not found";
        }
        ctx.writeAndFlush(result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

package cn.nu11cat.proxy;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.protocol.HttpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static <T> T getProxy(Class interfaceClass) {

        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(), method.getParameterTypes(), args);

                HttpClient httpClient = new HttpClient();
                String result = httpClient.send("localhost", 8080, invocation);

                return result;
            }
        });

        return (T) proxyInstance;
    }
}

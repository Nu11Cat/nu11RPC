package cn.nu11cat.proxy;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.common.URL;
import cn.nu11cat.loadbalance.LoadBalance;
import cn.nu11cat.protocol.HttpClient;
import cn.nu11cat.register.MapRemoteRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyFactory {

    public static <T> T getProxy(Class interfaceClass) {

        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(), method.getParameterTypes(), args);

                HttpClient httpClient = new HttpClient();

                //服务发现
                List<URL> list = MapRemoteRegister.get(interfaceClass.getName());

                //服务调用
                String result = null;
                List<URL> invokedUrls = new ArrayList<>();

                int max = 3;
                while(max>0){

                    //负载均衡
                    list.remove(invokedUrls);
                    URL url = LoadBalance.random(list);
                    invokedUrls.add(url);

                    try {
                        result = httpClient.send(url.getHostname(), url.getPort(), invocation);
                    } catch (Exception e) {

                        if(max-- != 0) continue;


                        //服务容错
                        //HelloServiceErrorCallback
                        return "服务调用出错";
                    }
                }

                return result;
            }
        });

        return (T) proxyInstance;
    }
}

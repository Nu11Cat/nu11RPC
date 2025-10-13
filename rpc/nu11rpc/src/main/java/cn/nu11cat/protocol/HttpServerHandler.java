package cn.nu11cat.protocol;

import cn.nu11cat.common.Invocation;
import cn.nu11cat.register.LocalRegister;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServerHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // 1. 解析 JSON 请求体
            Invocation invocation = MAPPER.readValue(req.getInputStream(), Invocation.class);

            // 2. 反射调用本地实现类
            Class<?> implClass = LocalRegister.get(invocation.getInterfaceName(), "1.0");
            if (implClass == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("service not found");
                return;
            }

            Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), invocation.getParameters());

            // 3. 返回结果（JSON）
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json; charset=UTF-8");
            MAPPER.writeValue(resp.getOutputStream(), result);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                resp.getWriter().write("server error: " + e.getMessage());
            } catch (Exception ignored) {}
        }
    }
}

package cn.nu11cat.common;

import java.io.Serializable;

public class Invocation implements Serializable {


    public Invocation() {}

    public Invocation(String interfaceName, String methodName, Class[] parameterTypes, Object[] parameters) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    private int connectTimeout = 1000;  // 连接超时（毫秒）
    private int readTimeout = 3000;     // 读取超时（毫秒）

    private String interfaceName;
    private String methodName;
    private Class[] parameterTypes;
    private Object[] parameters;

    // ---- getter / setter ----
    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }

    public String getInterfaceName() { return interfaceName; }
    public void setInterfaceName(String interfaceName) { this.interfaceName = interfaceName; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public Class[] getParameterTypes() { return parameterTypes; }
    public void setParameterTypes(Class[] parameterTypes) { this.parameterTypes = parameterTypes; }

    public Object[] getParameters() { return parameters; }
    public void setParameters(Object[] parameters) { this.parameters = parameters; }
}

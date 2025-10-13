package cn.nu11cat.protocol;

import cn.nu11cat.common.Invocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpClient {
    private static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    private static final int DEFAULT_READ_TIMEOUT = 3000;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String send(String hostname, int port, Invocation invocation) throws IOException {
        return send(hostname, port, invocation, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    public String send(String hostname, int port, Invocation invocation,
                       int connectTimeout, int readTimeout) throws IOException {

        URL url = new URL("http", hostname, port, "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // 写请求体（JSON 格式）
        try (OutputStream os = conn.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            MAPPER.writeValue(writer, invocation);
        }

        // 读取响应
        int status = conn.getResponseCode();
        InputStream in = (status >= 400 ? conn.getErrorStream() : conn.getInputStream());
        if (in == null) return "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }
}

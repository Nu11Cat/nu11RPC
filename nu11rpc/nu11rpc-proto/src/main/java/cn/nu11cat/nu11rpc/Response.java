package cn.nu11cat.nu11rpc;

import lombok.Data;

@Data
public class Response {
    private  int code = 0;
    private String message = "OK";
    private Object data;
}

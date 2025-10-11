package cn.nu11cat.nu11rpc;

import lombok.Data;

@Data
public class Request {
    private ServiceDescriptor service;
    private Object[] parameters;
}

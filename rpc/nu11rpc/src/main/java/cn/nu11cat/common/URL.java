package cn.nu11cat.common;

import java.io.Serializable;

public class URL implements Serializable {

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public URL(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    private String hostname;
    private Integer port;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    private Integer weight;

    public URL(String hostname, Integer port, Integer weight) {
        this.hostname = hostname;
        this.port = port;
        this.weight = weight;
    }
}

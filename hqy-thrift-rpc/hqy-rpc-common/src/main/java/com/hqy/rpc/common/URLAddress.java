package com.hqy.rpc.common;

import com.google.common.base.Objects;
import com.hqy.util.IpUtil;

import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 11:18
 */
public class URLAddress implements Serializable {

    private static final long serialVersionUID = 9179194905000296605L;

    /**
     * host
     */
    protected String host;

    /**
     * 端口
     */
    protected int port;

    /**
     * 时间戳
     */
    protected transient long timestamp;

    /**
     * 完整连接地址
     */
    protected transient String rawAddress;


    public URLAddress(String host, int port) {
        this(host, port, null);

    }

    public URLAddress(String host, int port, String rawAddress) {
        this.host = host;
        port = Math.max(port, 0);
        this.port = port;
        this.timestamp = System.currentTimeMillis();
        this.rawAddress = rawAddress;
    }

    public String getHost() {
        return host;
    }

    public URLAddress setHost(String host) {
        return new URLAddress(host, port, rawAddress);
    }

    public int getPort() {
        return port;
    }

    public URLAddress setPort(int port) {
        return new URLAddress(host, port, rawAddress);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public URLAddress setAddress(String host, int port) {
        return new URLAddress(host, port, rawAddress);
    }


    public String getAddress() {
        if (rawAddress == null) {
            rawAddress = getAddress(getHost(), getPort());
        }
        return rawAddress;
    }

    protected String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }

    public String getIp() {
        return IpUtil.getIpByHost(getHost());
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URLAddress that = (URLAddress) o;
        return port == that.port && Objects.equal(host, that.host) && Objects.equal(rawAddress, that.rawAddress);
    }

    @Override
    public int hashCode() {
        return host.hashCode() * 31 + port;
    }
}

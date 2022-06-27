package com.hqy.rpc.common;

import cn.hutool.core.map.MapUtil;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.IpUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 16:44
 */
public class Remote implements Serializable {

    private static final long serialVersionUID = -508803540161945493L;

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

    /**
     * remote protocol
     */
    protected String protocol;

    /**
     * username to connect remote registry.
     */
    protected String username;

    /**
     * password to connect remote registry.
     */
    protected String password;

    /**
     * expansion params
     */
    protected Map<String, Object> ex = MapUtil.newHashMap(8);


    public Remote(String host, int port) {
        this(host, port, null);
    }


    public Remote(String host, int port, String rawAddress) {
        this(host, port, rawAddress, null, null, null);
    }


    public Remote(String host, int port, String rawAddress, String protocol, String username, String password) {
        this.host = host;
        port = Math.max(port, 0);
        this.port = port;
        this.rawAddress = rawAddress;
        this.protocol = protocol;
        this.timestamp = System.currentTimeMillis();
        this.username = username;
        this.password = password;
    }


    public Remote(String host, int port, String rawAddress, String protocol, String username, String password, Map<String, Object> ex) {
        this.host = host;
        port = Math.max(port, 0);
        this.port = port;
        this.rawAddress = rawAddress;
        this.protocol = protocol;
        this.timestamp = System.currentTimeMillis();
        this.username = username;
        this.password = password;
        this.ex = ex;
    }

    public String getHost() {
        return host;
    }

    public Remote setHost(String host) {return new Remote(host, port, rawAddress); }

    public int getPort() { return port; }

    public Remote setPort(int port) { return new Remote(host, port, rawAddress); }

    public long getTimestamp() { return timestamp; }

    public Remote setRemote(String host, int port) { return new Remote(host, port, rawAddress); }

    public String getAddress() {
        if (rawAddress == null) {
            rawAddress = getAddress(getHost(), getPort());
        }
        return rawAddress;
    }

    protected String getAddress(String host, int port) {
        return port <= 0 ? host : host + StringConstants.Symbol.COLON + port;
    }

    public String getIp() {
        return IpUtil.getIpByHost(getHost());
    }


    public String getProtocol() {
        return protocol;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getEx() {
        return ex;
    }




    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("host", host)
                .append("port", port)
                .append("timestamp", timestamp)
                .append("rawAddress", rawAddress)
                .append("protocol", protocol)
                .append("username", username)
                .append("password", password)
                .append("ex", ex)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Remote remote = (Remote) o;
        return port == remote.port  && Objects.equals(host, remote.host) && Objects.equals(rawAddress, remote.rawAddress) && Objects.equals(protocol, remote.protocol) && Objects.equals(username, remote.username) && Objects.equals(password, remote.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, rawAddress, protocol, username, password);
    }
}

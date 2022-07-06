package com.hqy.rpc.common;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.IpUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * server connection 2 registry information
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 16:44
 */
public class ConnectionInfo implements Serializable {

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
     * username to connect remote registry.
     */
    protected String username;

    /**
     * password to connect remote registry.
     */
    protected String password;




    public ConnectionInfo(String host, int port) {
        this(host, port, null);
    }


    public ConnectionInfo(String host, int port, String rawAddress) {
        this(host, port, rawAddress, null, null);
    }


    public ConnectionInfo(String host, int port, String rawAddress, String username, String password) {
        this.host = host;
        port = Math.max(port, 0);
        this.port = port;
        this.rawAddress = rawAddress;
        this.timestamp = System.currentTimeMillis();
        this.username = username;
        this.password = password;
    }


    public String getHost() {
        return host;
    }

    public ConnectionInfo setHost(String host) {return new ConnectionInfo(host, port, rawAddress); }

    public int getPort() { return port; }

    public ConnectionInfo setPort(int port) { return new ConnectionInfo(host, port, rawAddress); }

    public long getTimestamp() { return timestamp; }

    public ConnectionInfo setRemote(String host, int port) { return new ConnectionInfo(host, port, rawAddress); }

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



    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("host", host)
                .append("port", port)
                .append("timestamp", timestamp)
                .append("rawAddress", rawAddress)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionInfo connectionInfo = (ConnectionInfo) o;
        return port == connectionInfo.port  && Objects.equals(host, connectionInfo.host) && Objects.equals(rawAddress, connectionInfo.rawAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, rawAddress);
    }


}

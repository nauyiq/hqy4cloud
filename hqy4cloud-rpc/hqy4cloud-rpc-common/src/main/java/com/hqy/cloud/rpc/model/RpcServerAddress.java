package com.hqy.cloud.rpc.model;

import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * rpc address.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/8
 */
public class RpcServerAddress implements Serializable {
    @Serial
    private transient static final long serialVersionUID = 2101476891509440422L;

    /**
     * rpc server binding port.
     */
    private int port;

    /**
     * rpc server binding hostAddr (local ip).
     */
    private String hostAddr;


    public static RpcServerAddress createConsumerRpcServer() {
        return new RpcServerAddress(IpUtil.getHostAddress());
    }

    public static RpcServerAddress of(String ip) {
        return StringUtils.isBlank(ip) ?  createConsumerRpcServer() : new RpcServerAddress(ip);
    }

    public RpcServerAddress() {
    }

    public RpcServerAddress(String hostAddr) {
        this.hostAddr = hostAddr;
    }

    public RpcServerAddress(int port, String hostAddr) {
        this.port = port;
        this.hostAddr = hostAddr;
    }


    public int getPort() {
        return port;
    }

    public String getHostAddr() {
        return hostAddr;
    }


    public void setPort(int port) {
        this.port = port;
    }

    public void setHostAddr(String hostAddr) {
        this.hostAddr = hostAddr;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcServerAddress that = (RpcServerAddress) o;
        return port == that.port && hostAddr.equals(that.hostAddr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, hostAddr);
    }
}

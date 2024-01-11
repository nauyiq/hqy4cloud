package com.hqy.cloud.rpc.model;

import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.NetUtils;
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

    /**
     * program id
     */
    private transient int pid;

    public static RpcServerAddress createConsumerRpcServer() {
        return new RpcServerAddress(IpUtil.getHostAddress(), NetUtils.getProgramId());
    }

    public static RpcServerAddress of(String ip) {
        return StringUtils.isBlank(ip) ?  createConsumerRpcServer() : new RpcServerAddress(ip, NetUtils.getProgramId());
    }

    public RpcServerAddress() {
    }

    public RpcServerAddress(int port, String hostAddr, int pid) {
        this.port = port;
        this.hostAddr = hostAddr;
        this.pid = pid;
    }

    public RpcServerAddress(String hostAddr, int pid) {
        this.hostAddr = hostAddr;
        this.pid = pid;
        this.port = 0;
    }

    public RpcServerAddress(UsingIpPort uip) {
        this.hostAddr = uip.getHostAddr();
        this.pid = uip.getPid();
        this.port = uip.getRpcPort();
    }

    public int getPort() {
        return port;
    }

    public String getHostAddr() {
        return hostAddr;
    }


    public int getPid() {
        return pid;
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
        return port == that.port && hostAddr.equals(that.hostAddr) &&  pid == that.pid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, hostAddr, pid);
    }
}

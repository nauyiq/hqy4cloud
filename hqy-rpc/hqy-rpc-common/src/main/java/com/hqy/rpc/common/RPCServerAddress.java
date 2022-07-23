package com.hqy.rpc.common;

import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.util.IpUtil;
import com.hqy.util.NetUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/8 10:50
 */
public class RPCServerAddress implements Serializable {
    private transient static final long serialVersionUID = 2101476891509440422L;

    /**
     * rpc server binding port.
     */
    private final int port;

    /**
     * rpc server binding hostAddr (local ip).
     */
    private final String hostAddr;

    /**
     * program id
     */
    private final transient int pid;

    public static RPCServerAddress createConsumerRpcServer() {
        return new RPCServerAddress(IpUtil.getHostAddress(), NetUtils.getProgramId());
    }

    public RPCServerAddress(int port, String hostAddr, int pid) {
        this.port = port;
        this.hostAddr = hostAddr;
        this.pid = pid;
    }

    public RPCServerAddress(String hostAddr, int pid) {
        this.hostAddr = hostAddr;
        this.pid = pid;
        this.port = 0;
    }

    public RPCServerAddress(UsingIpPort uip) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("port", port)
                .append("hostAddr", hostAddr)
                .append("pid", pid)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPCServerAddress that = (RPCServerAddress) o;
        return port == that.port && hostAddr.equals(that.hostAddr) &&  pid == that.pid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, hostAddr, pid);
    }
}

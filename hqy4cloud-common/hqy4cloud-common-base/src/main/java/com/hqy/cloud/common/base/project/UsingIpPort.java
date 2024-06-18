package com.hqy.cloud.common.base.project;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 进程服务的ip端口, 进程号信息
 * @author qy
 * @date  2021-08-13
 */
@Data
public class UsingIpPort implements Serializable {

    @Serial
    private static final long serialVersionUID = 5671807921455826403L;

    /**
     * 服务进程ip
     */
    private String hostAddr;

    /**
     * 服务端口
     */
    private int port;

    /**
     * rpc端口 -1表示没有注册rpc服务
     */
    private int rpcPort;

    /**
     * 服务进程编号
     */
    private int pid;

    /**
     * socket端口 -1表示没有注册socket服务
     */
    private int socketPort;

    public UsingIpPort() {
    }

    public UsingIpPort(int pid) {
        this.pid = pid;
    }

    public UsingIpPort(String hostAddr, int port) {
        this.hostAddr = hostAddr;
        this.port = port;
    }

    public UsingIpPort(String hostAddr, int port, int rpcPort, int pid) {
        this.hostAddr = hostAddr;
        this.port = port;
        this.rpcPort = rpcPort;
        this.pid = pid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("ip", hostAddr)
                .append("port", port)
                .toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pid;
        result = prime * result + ((hostAddr == null) ? 0 : hostAddr.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UsingIpPort other = (UsingIpPort) obj;
        if (pid != other.pid) {
            return false;
        }
        if (hostAddr == null) {
            if (other.hostAddr != null) {
                return false;
            }
        } else if (!hostAddr.equals(other.hostAddr)) {
            return false;
        }
        return port == other.port;
    }
}

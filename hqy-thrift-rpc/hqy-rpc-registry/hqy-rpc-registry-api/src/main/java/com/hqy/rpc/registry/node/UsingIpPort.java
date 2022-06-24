package com.hqy.rpc.registry.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 进程服务的ip端口, 进程号信息
 * @author qy
 * @date  2021-08-13 10:02
 */
@Data
@ToString
@NoArgsConstructor
public class UsingIpPort implements Serializable {

    private static final long serialVersionUID = 5671807921455826403L;

    /**
     * 服务进程ip
     */
    private String ip;

    /**
     * 服务端口
     */
    private int port;

    /**
     * rpc端口 -1表示没有注册rpc服务
     */
    private int rpcPort = -1;

    /**
     * 环境
     */
    private String env;

    /**
     * 服务进程编号
     */
    private int index;

    /**
     * socket端口 -1表示没有注册socket服务
     */
    private int socketPort = -1;


    public UsingIpPort(String ip, int port, int rpcPort, int index) {
        this.ip = ip;
        this.port = port;
        this.rpcPort = rpcPort;
        this.index = index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
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
        if (index != other.index) {
            return false;
        }
        if (ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!ip.equals(other.ip)) {
            return false;
        }
        return port == other.port;
    }
}

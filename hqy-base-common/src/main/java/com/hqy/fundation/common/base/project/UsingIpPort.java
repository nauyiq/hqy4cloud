package com.hqy.fundation.common.base.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 进程服务的ip端口, 进程号信息
 * @author qy
 * @date  2021-08-13 10:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsingIpPort implements Serializable {

    @JsonIgnore
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
     * rpc端口 -1表示不是服务的提供者
     */
    private int rpcPort = -1;

    /**
     * socket服务端口 -1表示当前服务没有提供socket服务
     */
    private int socketPort = -1;

    /**
     * 服务进程编号
     */
    private int index;

    /**
     * 环境
     */
    private String env;


    public UsingIpPort(String ip, int port, int index) {
        this.ip = ip;
        this.port = port;
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

package com.hqy.rpc.regist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 进程服务的ip端口, 进程号信息
 *
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 10:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsingIpPort implements Serializable {

    /**
     * 服务进程ip
     */
    private String ip;

    /**
     * 服务端口
     */
    private int port;

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

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
        return result;
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UsingIpPort other = (UsingIpPort) obj;
        if (index != other.index)
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port != other.port)
            return false;
        return true;
    }
}

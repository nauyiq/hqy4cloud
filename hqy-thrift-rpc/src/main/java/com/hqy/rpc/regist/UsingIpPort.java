package com.hqy.rpc.regist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 进程服务的ip端口, 进程号信息
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
}

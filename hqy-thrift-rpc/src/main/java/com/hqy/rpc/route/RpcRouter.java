package com.hqy.rpc.route;

import com.hqy.fundation.common.base.project.UsingIpPort;

import java.util.List;

/**
 * @author qy
 * @date 2021-08-13 11:16
 */
public interface RpcRouter {

    /**
     * 获取灰度节点列表
     * @return 节点列表
     */
    List<UsingIpPort>  getGrayProviders();

    /**
     * 获取白度节点列表
     * @return 节点列表
     */
    List<UsingIpPort> getWhiteProviders();

    /**
     * 设置灰度节点列表
     * @param usingPorts 节点列表
     */
    void setGrayProviders(List<UsingIpPort> usingPorts);

    /**
     * 设置白度节点列表
     * @param usingPorts 节点列表
     */
    void setWhiteProviders(List<UsingIpPort> usingPorts);

    /**
     * 获取所有可连接节点列表
     * @return 节点列表
     */
    List<UsingIpPort> getAllProviders();

}

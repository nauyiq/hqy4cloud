package com.hqy.rpc.registry.client;

import com.hqy.rpc.common.URL;

/**
 * The factory to create {@link ServerDiscovery}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 15:40
 */
public interface ServerDiscoveryFactory {

    /**
     * 根据注册信息获取客户端调用者视角
     * @param registryUrl 注册信息
     * @return            客户端调用者视角
     */
    ServerDiscovery gerServerDiscovery(URL registryUrl);




}

package com.hqy.cloud.gateway.loadbalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * 长连接路由器.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 15:22
 */
public interface WebsocketRouter {

    /**
     * 路由.
     * @param serviceName 服务名
     * @param hash        hash值
     * @param instances   实例列表
     * @return            实例
     */
    ServiceInstance router(String serviceName, int hash, List<ServiceInstance> instances);


}

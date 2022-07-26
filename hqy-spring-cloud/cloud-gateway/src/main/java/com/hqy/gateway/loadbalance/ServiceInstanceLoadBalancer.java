package com.hqy.gateway.loadbalance;

import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.hqy.util.AssertUtil;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/28 12:27
 */
public abstract class ServiceInstanceLoadBalancer implements LoadBalancer<ServiceInstance>{

    public static NacosServiceInstance copy(int socketPort, ServiceInstance serviceInstance) {
        AssertUtil.notNull(serviceInstance, "ServiceInstance should not be null.");
        NacosServiceInstance nacosServiceInstance = new NacosServiceInstance();
        nacosServiceInstance.setHost(serviceInstance.getHost());
        nacosServiceInstance.setServiceId(serviceInstance.getServiceId());
        nacosServiceInstance.setMetadata(serviceInstance.getMetadata());
        nacosServiceInstance.setPort(socketPort);
        nacosServiceInstance.setSecure(serviceInstance.isSecure());
        return nacosServiceInstance;
    }


}

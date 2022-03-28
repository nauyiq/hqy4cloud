package com.hqy.gateway.route;

import org.springframework.cloud.client.ServiceInstance;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/28 12:27
 */
public interface ServiceInstanceLoadBalancer extends LoadBalancer<ServiceInstance>{
}

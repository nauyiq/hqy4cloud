package com.hqy.cloud.gateway.loadbalance;


import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 负载均衡器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/28 12:25
 */
public interface LoadBalancer<T> {

    /**
     * 选择一个合适的客户端
     * Choose the next server based on the load balancing algorithm.
     * @param exchange - ServerWebExchange
     * @param discoveryClient - DiscoveryClient
     * @return - mono of response
     */
    @SuppressWarnings("deprecation")
    Mono<Response<T>> choose(ServerWebExchange exchange, DiscoveryClient discoveryClient);

}

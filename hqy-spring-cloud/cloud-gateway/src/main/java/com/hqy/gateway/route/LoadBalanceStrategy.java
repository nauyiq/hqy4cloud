package com.hqy.gateway.route;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/25 17:37
 */
public interface LoadBalanceStrategy {

    /**
     * 选择返回的实例
     * @param request ServerWebExchange
     * @return 相应的实例
     */
    @SuppressWarnings("deprecation")
    Mono<Response<ServiceInstance>> choose(Request request);

}

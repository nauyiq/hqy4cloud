package com.hqy.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;

/**
 * 增加hash值负载过滤器 继承ReactiveLoadBalancerClientFilter.java
 * 增加自定义hash负载策略
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/25 16:52
 */
public class ReactiveCustomerLoadBalancerClientFilter extends ReactiveLoadBalancerClientFilter {

    private static final String STRATEGY = "hash";

    private static final Logger log = LoggerFactory.getLogger(ReactiveCustomerLoadBalancerClientFilter.class);


    public ReactiveCustomerLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory, LoadBalancerProperties properties) {
        super(clientFactory, properties);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null) {
            //直接执行下一条责任链
            return chain.filter(exchange);
        }
        if ("lb".equals(url.getScheme()) || "lb".equals(schemePrefix)) {
            //走父类的负载逻辑 即默认的lb负载逻辑
            return super.filter(exchange, chain);
        }

        //暂时写死走hash策略


        return super.filter(exchange, chain);


    }
}

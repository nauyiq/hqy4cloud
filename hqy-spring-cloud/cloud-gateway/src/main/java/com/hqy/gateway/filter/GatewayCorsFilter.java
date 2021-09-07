package com.hqy.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * cors网关全局过滤器
 * @author qy
 * @create 2021/7/25 22:42
 */
@Component
@Slf4j
public class GatewayCorsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        ServerHttpResponse response = exchange.getResponse();

        if ("/favicon.ico".equals(path)) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        if (!CorsUtils.isCorsRequest(request)) {
            return chain.filter(exchange);
        }

        HttpHeaders requestHeaders = request.getHeaders();
        HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();

        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
        headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());

        if (requestMethod != null) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
        }

        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 77;
    }
}

package com.hqy.gateway.filter;

import com.hqy.base.common.base.lang.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import static com.hqy.gateway.config.Constants.CORS_FILTER_ORDER;

/**
 * cors网关全局过滤器
 * @author qiyuan.hong
 * @date  2021/7/25 22:42
 */
@Slf4j
@Component
public class GatewayCorsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        ServerHttpResponse response = exchange.getResponse();

        //如果是/favicon.io的路径允许访问
        if (StringConstants.FAVICON_ICO.equals(path)) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        //如果是socket.io项目 则放开piling请求的cors 原因是在socket.io netty内部已经处理了cors
        if (path.contains(StringConstants.WEBSOCKET_PATH) && request.getQueryParams().containsKey(StringConstants.Auth.SOCKET_AUTH_TOKEN)) {
            return chain.filter(exchange);
        }

        //如果请求是有效的 CORS，则返回 true
        if (!CorsUtils.isCorsRequest(request)) {
            return chain.filter(exchange);
        }

        HttpHeaders requestHeaders = request.getHeaders();
        HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
        HttpHeaders headers = response.getHeaders();
        //SETTING CORS RESPONSE HEADER.
        if (CollectionUtils.isEmpty(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))) {
            String origin = requestHeaders.getOrigin();
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, StringUtils.isBlank(origin) ? "*" : origin);
        }
        if (CollectionUtils.isEmpty(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS))) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        if (CollectionUtils.isEmpty(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))) {
            headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
        }
        if (requestMethod != null && CollectionUtils.isEmpty(headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
        }

        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return CORS_FILTER_ORDER;
    }
}

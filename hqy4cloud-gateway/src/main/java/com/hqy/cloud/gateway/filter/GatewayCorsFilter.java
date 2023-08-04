package com.hqy.cloud.gateway.filter;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.gateway.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * cors网关全局过滤器
 * @author qiyuan.hong
 * @date  2021/7/25 22:42
 */
@Slf4j
//@Component
public class GatewayCorsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        ServerHttpResponse response = exchange.getResponse();

        //如果请求是有效的 CORS，则返回 true
        if (!CorsUtils.isCorsRequest(request)) {
            return chain.filter(exchange);
        }

        HttpHeaders requestHeaders = request.getHeaders();
        HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
        HttpHeaders headers = response.getHeaders();
        HttpHeaders responseHeaders = response.getHeaders();

        requestHeaders.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        requestHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://172.16.42.73:9527");

        //Access-Control-Allow-Origin
//        checkResponseHeader(responseHeaders, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        //ACCESS_CONTROL_ALLOW_CREDENTIALS
        checkResponseHeader(responseHeaders, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "false");
        //ACCESS_CONTROL_ALLOW_HEADERS
//        checkResponseHeader(responseHeaders, HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
        //ACCESS_CONTROL_ALLOW_METHODS
        /*if (requestMethod != null) {
            checkResponseHeader(responseHeaders, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
        }*/
        return chain.filter(exchange);
    }



    private void checkResponseHeader(HttpHeaders responseHeaders, String header, String value) {
        List<String> headerValue = responseHeaders.get(header);
        if (CollectionUtils.isEmpty(headerValue)) {
            responseHeaders.add(header, value);
        } else if (headerValue.size() != 1) {
            responseHeaders.remove(header);
            responseHeaders.add(header, value);
        } else if (headerValue.stream().anyMatch(StringConstants.NULL::equals)) {
            responseHeaders.remove(header);
        }
    }

    private void checkResponseHeader(HttpHeaders responseHeaders, String header, List<String> value) {
        List<String> headerValue = responseHeaders.get(header);
        if (CollectionUtils.isEmpty(headerValue)) {
            responseHeaders.addAll(header, value);
        } else if (headerValue.size() != 1) {
            responseHeaders.remove(header);
            responseHeaders.addAll(header, value);
        }
    }


    @Override
    public int getOrder() {
        return Constants.CORS_FILTER_ORDER;
    }
}

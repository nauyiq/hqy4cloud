package com.hqy.filter;

import com.hqy.util.IpUtil;
import com.hqy.global.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 全球的http节流过滤器
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 16:42
 */
@Slf4j
public class GlobalHttpThrottleFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String url = request.getPath().pathWithinApplication().value();

        //1.静态资源放行
        //2.option请求放行
        if (RequestUtil.isStaticResource(url) || request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }





        String requestIp = IpUtil.getRequestIp(request);

        URI uri = request.getURI();


        return null;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

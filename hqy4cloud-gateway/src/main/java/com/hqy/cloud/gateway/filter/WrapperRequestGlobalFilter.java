package com.hqy.cloud.gateway.filter;

import com.hqy.cloud.common.swticher.ServerSwitcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.hqy.cloud.gateway.Constants.WRAPPER_REQUEST_FILTER_ORDER;

/**
 * 基于gateway webFlux 请求体内容被截断、不可重复读的包装过滤器
 * @author qy
 * @date  2021-07-30 12:00
 */
@Slf4j
@Component
public class WrapperRequestGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (ServerSwitcher.ENABLE_REPEAT_READABLE_HTTP_REQUEST_WRAPPER_FILTER.isOff()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        } else {
            DefaultDataBufferFactory defaultDataBufferFactory = new DefaultDataBufferFactory();
            DefaultDataBuffer defaultDataBuffer = defaultDataBufferFactory.allocateBuffer(0);
            //构建新数据流， 当body为空时，构建空流
            Flux<DataBuffer> bodyDataBuffer = exchange.getRequest().getBody().defaultIfEmpty(defaultDataBuffer);
            return DataBufferUtils.join(bodyDataBuffer)
                    .flatMap(dataBuffer -> {
                        DataBufferUtils.retain(dataBuffer);
                        Flux<DataBuffer> cachedFlux = Flux
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }
    }

    @Override
    public int getOrder() {
        return WRAPPER_REQUEST_FILTER_ORDER;
    }
}

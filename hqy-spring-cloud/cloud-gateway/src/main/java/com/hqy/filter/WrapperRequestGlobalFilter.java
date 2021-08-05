package com.hqy.filter;

import com.hqy.common.swticher.HttpGeneralSwitcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * 基于gateway webFlux 请求体内容被截断、不可重复读的包装过滤器
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-30 12:00
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class WrapperRequestGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (HttpGeneralSwitcher.ENABLE_REPEAT_READABLE_HTTP_REQUEST_WRAPPER_FILTER.isOff()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        Map<String, String> map = request.getQueryParams().toSingleValueMap();
        if (request.getMethod() == HttpMethod.OPTIONS || Objects.isNull(request.getHeaders().getContentType())) {
            //1.Content-Type为空放开
            //2.OPTIONS 请求放开
            return chain.filter(exchange);
        } else {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        DataBufferUtils.retain(dataBuffer);
                        Flux<DataBuffer> cachedFlux = Flux
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
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
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

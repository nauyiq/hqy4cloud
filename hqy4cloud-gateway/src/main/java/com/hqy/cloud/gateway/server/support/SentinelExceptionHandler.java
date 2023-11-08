package com.hqy.cloud.gateway.server.support;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/25 14:30
 */
@Slf4j
public class SentinelExceptionHandler implements BlockRequestHandler {

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
        return ServerResponse.status(HttpStatus.FORBIDDEN)
                .body(BodyInserters.fromValue(R.failed(ResultCode.INTERFACE_LIMITED)));
    }
}

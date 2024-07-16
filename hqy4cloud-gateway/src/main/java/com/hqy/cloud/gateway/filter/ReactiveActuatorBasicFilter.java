package com.hqy.cloud.gateway.filter;

import com.hqy.cloud.account.response.AccountResultCode;
import com.hqy.cloud.actuator.filter.AbstractActuatorBasicFilter;
import com.hqy.cloud.actuator.service.BasicAuthorizationService;
import com.hqy.cloud.auth.utils.StaticEndpointAuthorizationManager;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.gateway.util.RequestUtil;
import com.hqy.cloud.gateway.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 网关reactive actuator basic过滤器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7 15:30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveActuatorBasicFilter extends AbstractActuatorBasicFilter implements WebFilter {
    private final BasicAuthorizationService basicAuthorizationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (CommonSwitcher.ENABLE_ACTUATOR_BASIC_AUTHORIZATION.isOff()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri = request.getURI().getPath();
        if (StaticEndpointAuthorizationManager.getInstance().isActuatorRequest(uri)) {
            String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (!basicAuthorizationService.isAuth(authorization)) {
                log.warn("Failed basic auth, authorization: {}, ip:{}", authorization, RequestUtil.getIpAddress(request));
                return Mono.defer(() -> {
                    DataBuffer buffer = ResponseUtil.outputBuffer(R.failed(AccountResultCode.INVALID_CLIENT_OR_SECRET), response, HttpStatus.FORBIDDEN);
                    return response.writeWith(Flux.just(buffer));
                });
            }
        }
        return chain.filter(exchange);
    }


}

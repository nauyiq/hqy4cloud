package com.hqy.cloud.gateway.filter;

import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.gateway.Constants;
import com.hqy.cloud.gateway.util.RequestUtil;
import com.hqy.cloud.gateway.util.ResponseUtil;
import com.hqy.cloud.gateway.server.GatewayHttpThrottles;
import com.hqy.cloud.limiter.core.LimitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * HTTP节流过滤器 判断当前请求是否是安全的、ip是否超限等。
 * @author qiyuan.hong
 * @date  2021-07-27 16:42
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalHttpThrottleFilter implements GlobalFilter, Ordered {
    private final GatewayHttpThrottles httpThrottles;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getPath().pathWithinApplication().value();
        String uri = request.getURI().getPath();
        String requestIp = RequestUtil.getIpAddress(request);
        ServerHttpResponse httpResponse = exchange.getResponse();

        // 静态资源 | option请求 | 白名单
        if (RequestUtil.isStaticResource(url) || request.getMethod() == HttpMethod.OPTIONS ||
                httpThrottles.isWhiteURI(uri) || httpThrottles.isManualWhiteIp(requestIp)) {
            return chain.filter(exchange);
        }

        if (ServerSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff()) {
            //没有启用限流器...继续执行责任链
            return chain.filter(exchange);
        } else {
            LimitResult limitResult = httpThrottles.limitValue(request);
            if (limitResult.isNeedLimit()) {
                String resultTip = StringUtils.isBlank(limitResult.getTip()) ? ResultCode.ILLEGAL_REQUEST_LIMITED.message : limitResult.getTip();
                log.warn("HttpThrottled the request: {}, {}, {}.", requestIp, url, resultTip);
                MessageResponse response = new MessageResponse(false, resultTip, HttpStatus.FORBIDDEN.value());
                return Mono.defer(() -> {
                    DataBuffer buffer = ResponseUtil.outputBuffer(response, httpResponse, HttpStatus.FORBIDDEN);
                    return httpResponse.writeWith(Flux.just(buffer));
                });
            }
        }
        //继续执行责任链
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Constants.GLOBAL_HTTP_THROTTLE_FILER_ORDER;
    }
}

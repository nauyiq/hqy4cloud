package com.hqy.gateway.filter;

import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.swticher.HttpGeneralSwitcher;
import com.hqy.foundation.limit.LimitResult;
import com.hqy.gateway.server.GatewayHttpThrottles;
import com.hqy.gateway.util.RequestUtil;
import com.hqy.gateway.util.ResponseUtil;
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
        ServerHttpResponse httpResponse = exchange.getResponse();

        // 静态资源 | option请求 | uri白名单
        if (RequestUtil.isStaticResource(url) || request.getMethod() == HttpMethod.OPTIONS || httpThrottles.isWhiteURI(uri)) {
            return chain.filter(exchange);
        }

        String requestIp = RequestUtil.getIpAddress(request);
        //如果是人工白名单则放行
        if (httpThrottles.isManualWhiteIp(requestIp)) {
            return chain.filter(exchange);
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff()) {
            //没有启用限流器...继续执行责任链 ->
            return chain.filter(exchange);
        } else {
            //获取限流结果
            LimitResult limitResult = httpThrottles.limitValue(request);
            if (limitResult.isNeedLimit()) {
                log.warn("### Throttle limit current request: {}, {}", url, limitResult);
                String resultTip = StringUtils.isBlank(limitResult.getTip()) ? CommonResultCode.ILLEGAL_REQUEST_LIMITED.message : limitResult.getTip();
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
        return 1;
    }
}

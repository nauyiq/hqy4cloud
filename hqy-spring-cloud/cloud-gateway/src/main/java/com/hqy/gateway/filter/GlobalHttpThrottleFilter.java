package com.hqy.gateway.filter;

import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.bind.MessageResponse;
import com.hqy.fundation.common.result.CommonResultCode;
import com.hqy.fundation.common.swticher.HttpGeneralSwitcher;
import com.hqy.fundation.limit.LimitResult;
import com.hqy.gateway.server.GatewayHttpThrottles;
import com.hqy.gateway.util.ResponseUtil;
import com.hqy.util.JsonUtil;
import com.hqy.gateway.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 全球的http节流过滤器
 * @author qiyuan.hong
 * @date  2021-07-27 16:42
 */
@Slf4j
@Component
public class GlobalHttpThrottleFilter implements GlobalFilter, Ordered {

    /**
     * Http限流器组件
     */
    @Resource
    private GatewayHttpThrottles httpThrottles;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String url = request.getPath().pathWithinApplication().value();
        String uri = request.getURI().getPath();
        ServerHttpResponse httpResponse = exchange.getResponse();

        //静态资源放行和option请求放行
        if (RequestUtil.isStaticResource(url) || request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // uri白名单方形
        if (httpThrottles.isWhiteURI(uri)) {
            return chain.filter(exchange);
        }

        //获取请求客户端的ip地址
        String requestIp = RequestUtil.getIpAddress(request);
        //如果是人工白名单则放行
        if (httpThrottles.isManualWhiteIp(requestIp)) {
            return chain.filter(exchange);
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff() &&
                HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff()) {
            //没有启用限流器...继续执行责任链 ->
            return chain.filter(exchange);
        } else {
            //获取限流结果
            LimitResult limitResult = httpThrottles.limitValue(request);
            if (limitResult.isNeedLimit()) {
                //TODO 记录嫌疑ip
                log.warn("### THROTTLE_HTTP_LIMIT_ERROR :{},{}", url, limitResult);
                //TODO 记录嫌疑ip
                //返回403 表示当前请求被封禁
                String resultTip = limitResult.getTip();
                if (StringUtils.isBlank(requestIp)) {
                    resultTip = CommonResultCode.ILLEGAL_REQUEST.message;
                }
                MessageResponse response = new MessageResponse(false, resultTip, 403);
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

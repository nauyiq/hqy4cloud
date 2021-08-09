package com.hqy.filter;

import com.hqy.common.bind.MessageResponse;
import com.hqy.common.swticher.HttpGeneralSwitcher;
import com.hqy.dto.LimitResult;
import com.hqy.server.GatewayHttpThrottles;
import com.hqy.util.JsonUtil;
import com.hqy.util.RequestUtil;
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
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 16:42
 */
@Slf4j
@Component
public class GlobalHttpThrottleFilter implements GlobalFilter, Ordered {

    @Resource
    private GatewayHttpThrottles httpThrottles;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String url = request.getPath().pathWithinApplication().value();
        String uri = request.getURI().getPath();

        ServerHttpResponse httpResponse = exchange.getResponse();

        if (RequestUtil.isStaticResource(url) || request.getMethod() == HttpMethod.OPTIONS) {
            //1.静态资源放行
            //2.option请求放行
            return chain.filter(exchange);
        }

        if (httpThrottles.isWhiteURI(uri)) {
            // uri白名单;
            return chain.filter(exchange);
        }

        String requestIp = RequestUtil.getIpAddress(request);
        if (httpThrottles.isManualWhiteIp(requestIp)) {
            //人工白名单
            return chain.filter(exchange);
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff() && HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff()) {
            //没有启用限流器...继续执行责任链 链条
            return chain.filter(exchange);
        } else {
            LimitResult limitResult = httpThrottles.limitValue(request);
            if (limitResult.isNeedLimit()) {
                //TODO 记录嫌疑ip
                log.warn("### THROTTLE_HTTP_LIMIT_ERROR :{},{}", url, limitResult);
                //TODO 记录嫌疑ip
                //返回403 表示当前请求被封禁
                String resultTip = limitResult.getTip();
                if (StringUtils.isBlank(requestIp)) {
                    resultTip = "Illegal Request, Limit a few minutes.";
                }
                MessageResponse response = new MessageResponse(false, resultTip, 403);
                return Mono.defer(() -> {
                    byte[] bytes = JsonUtil.toJson(response).getBytes(StandardCharsets.UTF_8);
                    httpResponse.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                    httpResponse.setStatusCode(HttpStatus.FORBIDDEN);
                    DataBuffer buffer = httpResponse.bufferFactory().wrap(bytes);
                    return httpResponse.writeWith(Flux.just(buffer));
                });
            } else {
                //TODO 可以异步对请求进行采集分析？？？ 采集责任链？
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

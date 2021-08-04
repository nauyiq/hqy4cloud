package com.hqy.filter;

import com.hqy.common.swticher.HttpGeneralSwitcher;
import com.hqy.dto.LimitResult;
import com.hqy.global.RequestUtil;
import com.hqy.limit.impl.GatewayHttpThrottles;
import com.hqy.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

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

        if (RequestUtil.isStaticResource(url) || request.getMethod() == HttpMethod.OPTIONS) {
            //1.静态资源放行
            //2.option请求放行
            return chain.filter(exchange);
        }

        if (httpThrottles.isWhiteURI(uri)) {
            // uri白名单;
            return chain.filter(exchange);
        }

        String requestIp = IpUtil.getRequestIp(request);
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
            }



        }





        return null;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

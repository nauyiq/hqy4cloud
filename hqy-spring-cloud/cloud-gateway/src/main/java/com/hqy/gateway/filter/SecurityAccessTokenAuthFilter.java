package com.hqy.gateway.filter;

import cn.hutool.core.net.URLEncoder;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.OauthRequestUtil;
import com.nimbusds.jose.JWSObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.hqy.gateway.config.Constants.TOKEN_AUTH_FILTER_ORDER;


/**
 * access token 全局过滤器
 * 解析token 讲token解析好的数据重新put进请求头 后续调用链则无需解析token
 * @author qiyuan.hong
 * @date 2022-03-14 11:51
 */
@Component
public class SecurityAccessTokenAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(SecurityAccessTokenAuthFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(StringConstants.Auth.AUTHORIZATION_KEY);
        if (StringUtils.isBlank(authorization)) {
            return chain.filter(exchange);
        } else if (!OauthRequestUtil.checkAuthorization(authorization)) {
            return chain.filter(exchange);
        }

        String realToken = authorization.replace(StringConstants.Auth.JWT_PREFIX, Strings.EMPTY);
        try {
            String payload = JWSObject.parse(realToken).getPayload().toString();
            if (StringUtils.isBlank(payload)) {
                return chain.filter(exchange);
            } else {
                // 从token中解析用户信息并设置到Header中去
                request = request.mutate().header(StringConstants.Auth.JWT_PAYLOAD_KEY,
                        URLEncoder.DEFAULT.encode(payload, StandardCharsets.UTF_8)).build();
                exchange = exchange.mutate().request(request).build();
            }
        } catch (Throwable cause) {
            log.info("Failed execute to parse jwt obj, cause: {}.", cause.getMessage());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return TOKEN_AUTH_FILTER_ORDER;
    }
}


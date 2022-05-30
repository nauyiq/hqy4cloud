package com.hqy.gateway.filter;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.util.JsonUtil;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


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
        String authorization = request.getHeaders().getFirst(BaseStringConstants.Auth.AUTHORIZATION_KEY);
        if (StringUtils.isBlank(authorization) || !authorization.startsWith(BaseStringConstants.Auth.JWT_PREFIX)) {
            return chain.filter(exchange);
        } else if (authorization.startsWith(BaseStringConstants.Auth.BASIC_PREFIX)) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ Authorization Basic client Id, Authorization = {}", authorization);
            }
            return chain.filter(exchange);
        }

        try {
            String realToken = authorization.replace(BaseStringConstants.Auth.JWT_PREFIX, Strings.EMPTY);
            String payload = JWSObject.parse(realToken).getPayload().toString();
            if (StringUtils.isBlank(payload)) {
                return chain.filter(exchange);
            } else {
                // 从token中解析用户信息并设置到Header中去
                request = request.mutate().header(BaseStringConstants.Auth.JWT_PAYLOAD_KEY,
                        URLEncoder.encode(payload, StandardCharsets.UTF_8.name())).build();
                exchange = exchange.mutate().request(request).build();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}


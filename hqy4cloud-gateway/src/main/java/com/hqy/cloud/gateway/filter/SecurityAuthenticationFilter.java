package com.hqy.cloud.gateway.filter;

import cn.hutool.core.net.URLEncoder;
import com.hqy.cloud.auth.core.SecurityUser;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.web.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 将已通过security认证的用户信息传递给后续调用链中.
 * @author qiyuan.hong
 * @date 2022-03-14 11:51
 */
public class SecurityAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuthenticationFilter.class);

    @Nonnull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(StringConstants.Auth.AUTHORIZATION_KEY);
        if (StringUtils.isBlank(authorization) || !RequestUtil.checkAuthorization(authorization)) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext().filter(Objects::nonNull)
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication.isAuthenticated()) {
                        // 将已认证用户信息数据设置到HTTP Request Header 中.
                        try {
                            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                            AuthenticationInfo authenticationInfo = new AuthenticationInfo(securityUser.getId(), securityUser.getName(), securityUser.getEmail(),
                                    securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                            ServerHttpRequest serverHttpRequest = request.mutate().header(StringConstants.Auth.JWT_PAYLOAD_KEY,
                                    URLEncoder.DEFAULT.encode(JsonUtil.toJson(authenticationInfo), StandardCharsets.UTF_8)).build();
                            ServerWebExchange webExchange = exchange.mutate().request(serverHttpRequest).build();
                            return chain.filter(webExchange);
                        } catch (Throwable cause) {
                            log.error("Failed execute to setting authentication into chain. Authorization key: {}.", authentication, cause);
                        }
                    }
                    return chain.filter(exchange);
                });
    }
    
}


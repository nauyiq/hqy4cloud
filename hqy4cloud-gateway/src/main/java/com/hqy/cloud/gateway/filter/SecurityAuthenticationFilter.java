package com.hqy.cloud.gateway.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.auth.api.AuthUser;
import com.hqy.cloud.auth.api.support.DefaultAuthUser;
import com.hqy.cloud.auth.common.AuthException;
import com.hqy.cloud.auth.common.AuthUserHeaderConstants;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.security.core.SecurityAuthUser;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;


/**
 * 将已通过security认证的用户信息传递给后续调用链中.
 * @author qiyuan.hong
 * @date 2022-03-14
 */
public class SecurityAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuthenticationFilter.class);

    @Nonnull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization) || !AuthUtils.checkAuthorization(authorization)) {
            return chain.filter(exchange);
        }

        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOff()) {
            if (log.isDebugEnabled()) {
                log.debug("Not support security authentication diffuse inner user.");
            }
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey(AuthUserHeaderConstants.AUTH_USER)) {
            // 防止用户伪造请求头
            throw new AuthException(ResultCode.ILLEGAL_REQUEST_LIMITED.getCode());
        }

        return ReactiveSecurityContextHolder.getContext().filter(Objects::nonNull)
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication.isAuthenticated()) {
                        // 将已认证用户信息数据设置到HTTP Request Header 中.
                        try {
                            // 获取认证的用户信息
                            SecurityAuthUser securityUser = (SecurityAuthUser) authentication.getPrincipal();
                            Long id = securityUser.getId();
                            String username = securityUser.getUsername();
                            String email = securityUser.getEmail();
                            String phone = securityUser.getPhone();
                            UserRole userRole = securityUser.getUserRole();
                            List<String> authorities = securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
                            AuthUser authUser = new DefaultAuthUser(id, username, email, phone, userRole, authorities);
                            ServerHttpRequest serverHttpRequest = request.mutate().header(AuthUserHeaderConstants.AUTH_USER,
                                    Base64.encode(JSON.toJSONString(authUser))).build();
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


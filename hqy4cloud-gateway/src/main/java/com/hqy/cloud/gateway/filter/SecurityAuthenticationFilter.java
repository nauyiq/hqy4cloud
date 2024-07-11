package com.hqy.cloud.gateway.filter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import com.hqy.cloud.auth.common.AuthException;
import com.hqy.cloud.auth.common.AuthUserHeaderConstants;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.security.core.SecurityAuthUser;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.common.base.lang.StringConstants;
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


        // 移除请求头，防止客户端自己伪造用户信息
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey(AuthUserHeaderConstants.ID) || headers.containsKey(AuthUserHeaderConstants.EMAIL)
                || headers.containsKey(AuthUserHeaderConstants.PHONE)  || headers.containsKey(AuthUserHeaderConstants.USERNAME)
                || headers.containsKey(AuthUserHeaderConstants.ROLE) || headers.containsKey(AuthUserHeaderConstants.AUTHORITIES) ) {
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

                            // 将用户认证信息写入到请求头。
                            ServerHttpRequest serverHttpRequest = request.mutate()
                                    .header(AuthUserHeaderConstants.ID, Base64.encode(id.toString()))
                                    .header(AuthUserHeaderConstants.EMAIL, Base64.encode(email))
                                    .header(AuthUserHeaderConstants.PHONE, Base64.encode(phone))
                                    .header(AuthUserHeaderConstants.USERNAME, Base64.encode(username))
                                    .header(AuthUserHeaderConstants.AUTHORITIES, Base64.encode(userRole.name()))
                                    .header(AuthUserHeaderConstants.ROLE, Base64.encode(CollectionUtil.join(authorities, StringConstants.Symbol.COMMA))).build();

//                            ServerHttpRequest serverHttpRequest = request.mutate().header(JWT_PAYLOAD_KEY,
//                                    URLEncoder.DEFAULT.encode(JsonUtil.toJson(authenticationInfo), StandardCharsets.UTF_8)).build();
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


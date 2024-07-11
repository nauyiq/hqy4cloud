package com.hqy.cloud.gateway.server.auth;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.api.AuthenticationRequest;
import com.hqy.cloud.gateway.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 网关鉴权管理器 所有权限在此配置
 * @author qiyuan.hong
 * @date 2022-03-14 14:29
 */
@Slf4j
@Component
//@Deprecated
@RequiredArgsConstructor
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private final AuthPermissionService authPermissionService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        ReactAccess2Request authenticationRequest = new ReactAccess2Request(request);
        return mono
                .filter(Authentication::isAuthenticated)
                .map(authorities -> getAuthorizationDecision(authenticationRequest, authorities));
    }

    private AuthorizationDecision getAuthorizationDecision(ReactAccess2Request request, Authentication authentication) {
        Collection<? extends GrantedAuthority> authoritiesAuthorities = authentication.getAuthorities();
        if (CollectionUtils.isNotEmpty(authoritiesAuthorities)) {
            // 设置鉴权的权限列表到请求上下文中
            request.setAuthorities(authoritiesAuthorities.stream().map(GrantedAuthority::getAuthority).toList());
        }
        // 判断允许访问
        return new AuthorizationDecision(authPermissionService.isPermitRequest(request));
    }

    public static class ReactAccess2Request implements AuthenticationRequest {
        private final String requestIp;
        private final String requestUri;
        private final String requestUserAgent;
        private final String requestAccessToken;
        private final String method;
        @Setter
        private List<String> authorities;


        public ReactAccess2Request(ServerHttpRequest request) {
            this.requestIp = RequestUtil.getIpAddress(request);
            this.requestUri = request.getURI().getPath();
            this.requestUserAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
            this.requestAccessToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            this.method = Objects.requireNonNull(request.getMethod()).name();
        }

        @Override
        public String requestIp() {
            return requestIp;
        }

        @Override
        public String requestUri() {
            return requestUri;
        }

        @Override
        public String requestUserAgent() {
            return requestUserAgent;
        }

        @Override
        public String requestAccessToken() {
            return requestAccessToken;
        }

        @Override
        public String method() {
            return method;
        }

        @Override
        public List<String> authorities() {
            return this.authorities;
        }
    }


}

package com.hqy.gateway.server.auth;

import com.hqy.access.auth.AuthenticationRequest;
import com.hqy.access.auth.Oauth2Access;
import com.hqy.access.auth.RolesAuthoritiesChecker;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.gateway.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import java.util.stream.Collectors;

/**
 * 网关鉴权管理器 所有权限在此配置
 * @author qiyuan.hong
 * @date 2022-03-14 14:29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RolesAuthoritiesChecker rolesAuthoritiesChecker;
    private final Oauth2Access oauth2Access;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        //Option请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        AuthenticationRequest authenticationRequest = new ReactAccess2Request(request);
        if (oauth2Access.isPermitRequest(authenticationRequest)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 判断JWT中携带的用户角色是否有权限访问
        return mono
                .filter(Authentication::isAuthenticated)
                .map(authorities -> getAuthorizationDecision(authenticationRequest, authorities));
    }

    private AuthorizationDecision getAuthorizationDecision(AuthenticationRequest authenticationRequest, Authentication authorities) {
        Collection<? extends GrantedAuthority> authoritiesAuthorities = authorities.getAuthorities();
        if (CollectionUtils.isEmpty(authoritiesAuthorities)) {
            return new AuthorizationDecision(true);
        } else {
            List<String> roles = authoritiesAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            try {
                boolean isPermitAuthorities = rolesAuthoritiesChecker.isPermitAuthorities(roles, authenticationRequest);
                return new AuthorizationDecision(isPermitAuthorities);
            } catch (Throwable cause) {
                log.warn("Failed execute to check permit authorities, roles: {}.", roles, cause);
                return new AuthorizationDecision(false);
            }
        }
    }

    public static class ReactAccess2Request implements AuthenticationRequest {

        private final String requestIp;
        private final String requestUri;
        private final String requestUserAgent;
        private final String requestAccessToken;
        private final String method;

        public ReactAccess2Request(ServerHttpRequest request) {
            this.requestIp = RequestUtil.getIpAddress(request);
            this.requestUri = request.getURI().getPath();
            this.requestUserAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
            this.requestAccessToken = request.getHeaders().getFirst(StringConstants.Auth.AUTHORIZATION_KEY);
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
    }


}

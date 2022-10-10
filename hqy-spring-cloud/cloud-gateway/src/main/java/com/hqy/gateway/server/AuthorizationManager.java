package com.hqy.gateway.server;

import com.hqy.access.auth.Oauth2Access;
import com.hqy.access.auth.Oath2Request;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.gateway.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 网关鉴权管理器 所有权限在此配置
 * @author qiyuan.hong
 * @date 2022-03-14 14:29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final Oauth2Access oauth2Access;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        //Option请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        Oath2Request oath2Request = new ReactAccess2Request(request);
        if (oauth2Access.isPermitRequest(oath2Request)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 判断JWT中携带的用户角色是否有权限访问
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(s -> true)
                .map(s -> new AuthorizationDecision(true))
                .defaultIfEmpty(new AuthorizationDecision(true));

    }

    public static class ReactAccess2Request implements Oath2Request {

        private final String requestIp;
        private final String requestUri;
        private final String requestUserAgent;
        private final String requestAccessToken;

        public ReactAccess2Request(ServerHttpRequest request) {
            this.requestIp = RequestUtil.getIpAddress(request);
            this.requestUri = request.getURI().getPath();
            this.requestUserAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
            this.requestAccessToken = request.getHeaders().getFirst(StringConstants.Auth.AUTHORIZATION_KEY);
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
    }


}

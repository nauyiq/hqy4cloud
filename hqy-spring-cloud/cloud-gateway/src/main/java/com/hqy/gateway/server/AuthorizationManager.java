package com.hqy.gateway.server;

import com.hqy.auth.access.server.AuthorizationWhiteListManager;
import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.gateway.util.RequestUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;
import sun.security.krb5.internal.AuthContext;

import java.util.Set;

/**
 * 网关鉴权管理器 所有权限在此配置
 * @author qiyuan.hong
 * @date 2022-03-14 14:29
 */
@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        //Option请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        String path = request.getURI().getPath();
        String ipAddress = RequestUtil.getIpAddress(request);

        //检验是否可以白名单path
        if (permitAll(path)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // token必须以"bearer "为前缀
        String token = request.getHeaders().getFirst(BaseStringConstants.Auth.AUTHORIZATION_KEY);
        if (StringUtils.isNotBlank(token) && StringUtils.startsWithIgnoreCase(token, BaseStringConstants.Auth.JWT_PREFIX) ) {
            //白名单ip无需鉴权 放行
            Set<String> whiteIp = SpringContextHolder.getProjectContextInfo().getAttributeSetString(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY);
            if (CollectionUtils.isNotEmpty(whiteIp)) {
                for (String ip : whiteIp) {
                    if (ip.equals(ipAddress)) {
                        return Mono.just(new AuthorizationDecision(true));
                    }
                }
            }
        } else {
            return Mono.just(new AuthorizationDecision(false));
        }

        // 判断JWT中携带的用户角色是否有权限访问
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authority -> {
                    //FIXME 获取用户角色 校验权限
                    log.info("权限：{}", authority);
                    return true;
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));

    }

    private boolean permitAll(String path) {
        Set<String> whiteUri = AuthorizationWhiteListManager.getInstance().endpoints();
        return whiteUri.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, path));
    }


}

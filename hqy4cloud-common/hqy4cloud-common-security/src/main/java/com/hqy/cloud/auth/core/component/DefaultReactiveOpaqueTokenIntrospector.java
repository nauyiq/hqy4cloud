package com.hqy.cloud.auth.core.component;

import com.hqy.cloud.auth.base.lang.Oauth2ErrorCodesExpand;
import com.hqy.cloud.auth.core.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Objects;

/**
 * 自定义非开放式token内省.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/2 15:38
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultReactiveOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {
    private final OAuth2AuthorizationService authorizationService;

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (Objects.isNull(authorization)) {
            throw new InvalidBearerTokenException(Oauth2ErrorCodesExpand.INVALID_BEARER_TOKEN);
        }

        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(authorization.getAuthorizationGrantType())) {
            return Mono.just(new DefaultCredentialsOAuth2AuthenticatedPrincipal(authorization.getPrincipalName(),
                    authorization.getAttributes(), AuthorityUtils.NO_AUTHORITIES));
        }

        SecurityUser userDetails;
        try {
            Object principal = authorization.getAttributes().get(Principal.class.getName());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (SecurityUser) (usernamePasswordAuthenticationToken.getPrincipal());
            if (Objects.isNull(userDetails)) {
                throw new UsernameNotFoundException("Not found user by token = " + token);
            }
        } catch (UsernameNotFoundException notFoundException) {
            log.warn("Not found user {}.", notFoundException.getLocalizedMessage());
            throw notFoundException;
        } catch (Throwable cause) {
            log.error("Failed execute to resource server introspect token {}.", cause.getLocalizedMessage());
            throw cause;
        }
        return Mono.just(userDetails);
    }
}

package com.hqy.cloud.auth.support.core.email;

import com.hqy.cloud.auth.support.base.Oauth2ResourceOwnerBaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * 邮箱登录token信息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/17 17:31
 */
public class Oauth2ResourceOwnerEmailAuthenticationToken extends Oauth2ResourceOwnerBaseAuthenticationToken {
    public Oauth2ResourceOwnerEmailAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }
}

package com.hqy.cloud.auth.support.password;

import com.hqy.cloud.auth.support.base.Oauth2ResourceOwnerBaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 14:47
 */
public class Oauth2ResourceOwnerPasswordAuthenticationToken extends Oauth2ResourceOwnerBaseAuthenticationToken {
    public Oauth2ResourceOwnerPasswordAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }
}

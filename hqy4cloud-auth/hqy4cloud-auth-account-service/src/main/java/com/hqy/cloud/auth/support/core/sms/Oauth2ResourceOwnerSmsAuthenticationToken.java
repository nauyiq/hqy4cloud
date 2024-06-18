package com.hqy.cloud.auth.support.core.sms;

import com.hqy.cloud.auth.support.base.Oauth2ResourceOwnerBaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * 手机验证码登录token信息
 * @author qiyuan.hong
 * @date 2024/7/11
 */
public class Oauth2ResourceOwnerSmsAuthenticationToken extends Oauth2ResourceOwnerBaseAuthenticationToken {

    public Oauth2ResourceOwnerSmsAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }
}

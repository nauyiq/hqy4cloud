package com.hqy.cloud.auth.support.core;

import com.hqy.cloud.auth.common.SecurityConstants;
import com.hqy.cloud.auth.security.core.SecurityAuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

/**
 * token 输出增强
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 15:34
 */
public class DefaultOauth2TokenCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {

    /**
     * Customize the OAuth 2.0 Token attributes.
     * @param context the context containing the OAuth 2.0 Token attributes
     */
    @Override
    public void customize(OAuth2TokenClaimsContext context) {
        OAuth2TokenClaimsSet.Builder claims = context.getClaims();
        String clientId = context.getAuthorizationGrant().getName();
        claims.claim(SecurityConstants.CLIENT_ID, clientId);
        // 客户端模式不返回具体用户信息
        if (SecurityConstants.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType().getValue())) {
            return;
        }
        SecurityAuthUser securityUser = (SecurityAuthUser) context.getPrincipal().getPrincipal();
        claims.claim(SecurityConstants.USERNAME, securityUser.getName());
        claims.claim(SecurityConstants.ROLES, securityUser.authorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        claims.claim(SecurityConstants.ID, securityUser.getId());
    }
}

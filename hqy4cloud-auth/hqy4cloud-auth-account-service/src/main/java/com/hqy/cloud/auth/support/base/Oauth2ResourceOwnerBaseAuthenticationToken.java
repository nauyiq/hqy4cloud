package com.hqy.cloud.auth.support.base;

import com.hqy.cloud.util.AssertUtil;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.*;

/**
 * 自定义授权模式抽象基础类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 14:47
 */
public abstract class Oauth2ResourceOwnerBaseAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * 授权模式
     */
    private final AuthorizationGrantType authorizationGrantType;

    /**
     * 客户端认证
     */
    private final Authentication clientPrincipal;
    private final Set<String> scopes;
    private final Map<String, Object> additionalParameters;

    public Oauth2ResourceOwnerBaseAuthenticationToken(AuthorizationGrantType authorizationGrantType,
                                                      Authentication clientPrincipal,
                                                      Set<String> scopes,
                                                      Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        AssertUtil.notNull(authorizationGrantType, "AuthorizationGrantType should not be null.");
        AssertUtil.notNull(clientPrincipal, "ClientPrincipal should not be null.");

        this.authorizationGrantType = authorizationGrantType;
        this.clientPrincipal = clientPrincipal;
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections.unmodifiableMap(
                additionalParameters != null ? new HashMap<>(additionalParameters) : Collections.emptyMap());
    }


    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.clientPrincipal;
    }

    public AuthorizationGrantType getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }
}

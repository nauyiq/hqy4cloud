package com.hqy.cloud.auth.support.core.email;

import com.hqy.cloud.auth.base.lang.SecurityConstants;
import com.hqy.cloud.auth.support.base.Oauth2ResourceOwnerBaseAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Map;

/**
 * 邮箱认证核心处理
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/17 17:43
 */
@Slf4j
public class Oauth2ResourceOwnerEmailAuthenticationProvider extends Oauth2ResourceOwnerBaseAuthenticationProvider<Oauth2ResourceOwnerEmailAuthenticationToken> {

    /**
     * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
     * provided parameters.
     *
     * @param authenticationManager
     * @param authorizationService  the authorization service
     * @param tokenGenerator        the token generator
     * @param messageSource
     * @since 0.3.1
     */
    public Oauth2ResourceOwnerEmailAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator, MessageSource messageSource) {
        super(authenticationManager, authorizationService, tokenGenerator, messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        boolean supports = Oauth2ResourceOwnerEmailAuthenticationToken.class.isAssignableFrom(authentication);
        log.debug("supports authentication=" + authentication + " returning " + supports);
        return supports;
    }

    @Override
    public void checkClient(RegisteredClient registeredClient) {
        assert registeredClient != null;
        if (!registeredClient.getAuthorizationGrantTypes()
                .contains(new AuthorizationGrantType(SecurityConstants.EMAIL))) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters) {
        String email = (String) reqParameters.get(SecurityConstants.EMAIL);
        return new UsernamePasswordAuthenticationToken(email, null);
    }
}

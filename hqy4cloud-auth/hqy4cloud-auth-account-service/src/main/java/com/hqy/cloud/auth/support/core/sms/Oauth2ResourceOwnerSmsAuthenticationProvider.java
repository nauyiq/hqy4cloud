package com.hqy.cloud.auth.support.core.sms;

import com.hqy.cloud.auth.common.SecurityConstants;
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
 * 手机验证认证核心处理
 * @author qiyuan.hong
 * @date 2024/7/11
 */
@Slf4j
public class Oauth2ResourceOwnerSmsAuthenticationProvider extends Oauth2ResourceOwnerBaseAuthenticationProvider<Oauth2ResourceOwnerSmsAuthenticationToken> {
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
    public Oauth2ResourceOwnerSmsAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator, MessageSource messageSource) {
        super(authenticationManager, authorizationService, tokenGenerator, messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        boolean supports = Oauth2ResourceOwnerBaseAuthenticationProvider.class.isAssignableFrom(authentication);
        log.debug("supports authentication={} returning {}", authentication, supports);
        return supports;
    }

    @Override
    public void checkClient(RegisteredClient registeredClient) {
        assert registeredClient != null;
        if (!registeredClient.getAuthorizationGrantTypes()
                .contains(new AuthorizationGrantType(SecurityConstants.SMS))) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters) {
        String phone = (String) reqParameters.get(SecurityConstants.PHONE_PARAMETER_NAME);
        return new UsernamePasswordAuthenticationToken(phone, null);
    }
}

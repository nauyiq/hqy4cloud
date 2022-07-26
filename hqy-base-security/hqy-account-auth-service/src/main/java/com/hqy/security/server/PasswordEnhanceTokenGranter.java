package com.hqy.security.server;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * 密码增强模式
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/30 10:34
 */
public class PasswordEnhanceTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "password";

    private final AuthenticationManager authenticationManager;

    public PasswordEnhanceTokenGranter(AuthenticationManager authenticationManager,
                                       AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE );
    }


    protected PasswordEnhanceTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                          ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String username = requestParameters.get("username");
        String password = requestParameters.get("password");

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(requestParameters);

        try {
            userAuth = authenticationManager.authenticate(userAuth);
        }catch (AccountStatusException | BadCredentialsException ase) {
            throw new InvalidGrantException(ase.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        return new OAuth2Authentication(getRequestFactory().createOAuth2Request(client, tokenRequest), userAuth);

    }
}

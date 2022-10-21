package com.hqy.security.server;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * DefaultRefreshTokenGranter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 14:15
 */

public class DefaultRefreshTokenGranter extends RefreshTokenGranter {

    private static final String GRANT_TYPE = "refresh_token";
    private final AuthenticationManager authenticationManager;


    public DefaultRefreshTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    public DefaultRefreshTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String client_id = requestParameters.get("client_id");
        String client_secret = requestParameters.get("client_secret");

        Authentication userAuth = new UsernamePasswordAuthenticationToken(client_id, client_secret);
        ((AbstractAuthenticationToken) userAuth).setDetails(requestParameters);

        try {
            userAuth = authenticationManager.authenticate(userAuth);
        }catch (AccountStatusException | BadCredentialsException ase) {
            throw new InvalidGrantException(ase.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + client_id);
        }

        return new OAuth2Authentication(getRequestFactory().createOAuth2Request(client, tokenRequest), userAuth);
    }


}

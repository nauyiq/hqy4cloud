package com.hqy.security.server.email;

import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * EmailTokenGranter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 14:21
 */
public class EmailTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "email";

    protected EmailTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();

        return super.getOAuth2Authentication(client, tokenRequest);
    }
}

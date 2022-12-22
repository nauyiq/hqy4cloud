package com.hqy.security.core.client;

import com.hqy.auth.entity.AccountOauthClient;
import com.hqy.auth.service.AccountOauthClientTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:41
 */
@Service
@RequiredArgsConstructor
public class SecurityClientDetailsServiceImpl implements ClientDetailsService {


    private final AccountOauthClientTkService accountOauthClientTkService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        AccountOauthClient  accountOauthClient = accountOauthClientTkService.queryOne(new AccountOauthClient(clientId));
        if (Objects.isNull(accountOauthClient)) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }

        if (!accountOauthClient.getStatus()) {
            throw new DisabledException("Oauth client disable, clientId : " + clientId);
        }

        //Return Base implementation of
        BaseClientDetails clientDetails = new BaseClientDetails(accountOauthClient.getClientId(), accountOauthClient.getResourceIds(), accountOauthClient.getScope(),
                accountOauthClient.getAuthorizedGrantTypes(), accountOauthClient.getAuthorities(), accountOauthClient.getWebServerRedirectUri());
        clientDetails.setClientSecret(accountOauthClient.getClientSecret());
        clientDetails.setAccessTokenValiditySeconds(accountOauthClient.getAccessTokenValidity());
        clientDetails.setRefreshTokenValiditySeconds(accountOauthClient.getRefreshTokenValidity());

        return clientDetails;
    }
}

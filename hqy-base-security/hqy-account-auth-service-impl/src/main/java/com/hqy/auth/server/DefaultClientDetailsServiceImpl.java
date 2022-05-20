package com.hqy.auth.server;

import com.hqy.account.entity.AccountOauthClient;
import com.hqy.account.service.AccountOauthClientService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:41
 */
@Service
public class DefaultClientDetailsServiceImpl implements ClientDetailsService {

    @Resource
    private AccountOauthClientService accountOauthClientService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        AccountOauthClient accountOauthClient = accountOauthClientService.queryOne(new AccountOauthClient(clientId));
        if (Objects.isNull(accountOauthClient)) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
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

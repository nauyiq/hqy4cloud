package com.hqy.security.core.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:41
 */
@Service
@RequiredArgsConstructor
public class SecurityClientDetailsServiceImpl implements ClientDetailsService {
    private final AccountOauthClientTkService accountOauthClientTkService;
    private final Cache<String, AccountOauthClient> accountOauthClientCache =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterWrite(10, TimeUnit.MINUTES).build();

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        AccountOauthClient accountOauthClient = accountOauthClientCache.getIfPresent(clientId);
        if (Objects.isNull(accountOauthClient)) {
            accountOauthClient = accountOauthClientTkService.queryOne(new AccountOauthClient(clientId));
            if (Objects.isNull(accountOauthClient)) {
                throw new NoSuchClientException("No client with requested id: " + clientId);
            } else {
                 accountOauthClientCache.put(clientId, accountOauthClient);
            }
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

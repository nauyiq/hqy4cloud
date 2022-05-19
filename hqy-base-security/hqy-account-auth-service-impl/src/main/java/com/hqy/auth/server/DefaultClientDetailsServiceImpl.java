package com.hqy.auth.server;

import com.hqy.account.entity.OauthClient;
import com.hqy.account.service.OauthClientService;
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
    private OauthClientService oauthClientService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        OauthClient oauthClient = oauthClientService.queryById(clientId);
        if (Objects.isNull(oauthClient)) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }

        BaseClientDetails clientDetails = new BaseClientDetails(oauthClient.getId(), oauthClient.getResourceIds(), oauthClient.getScope(),
                oauthClient.getAuthorizedGrantTypes(), oauthClient.getAuthorities(), oauthClient.getWebServerRedirectUri());

        clientDetails.setClientSecret(oauthClient.getClientSecret());
        clientDetails.setAccessTokenValiditySeconds(oauthClient.getAccessTokenValidity());
        clientDetails.setRefreshTokenValiditySeconds(oauthClient.getRefreshTokenValidity());
        return clientDetails;
    }
}

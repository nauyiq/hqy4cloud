package com.hqy.cloud.auth.oauth.core;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.account.entity.OauthClient;
import com.hqy.cloud.auth.account.service.SysOauthClientDomainService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 10:06
 */
@Component
@RequiredArgsConstructor
public class DefaultRegisteredClientRepository implements RegisteredClientRepository {

    private final SysOauthClientDomainService sysOauthClientDomainService;

    /**
     * 刷新令牌有效期默认 30 天
     */
    private final static int REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30;

    /**
     * 请求令牌有效期默认 12 小时
     */
    private final static int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 12;


    /**
     * Saves the registered client.
     * <p>
     * IMPORTANT: Sensitive information should be encoded externally from the
     * implementation, e.g. {@link RegisteredClient#getClientSecret()}
     * @param registeredClient the {@link RegisteredClient}
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the registered client identified by the provided {@code id}, or
     * {@code null} if not found.
     * @param id the registration identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    public RegisteredClient findById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SneakyThrows
    public RegisteredClient findByClientId(String clientId) {
        OauthClient oauthClient = sysOauthClientDomainService.findByClientId(clientId);
        if (Objects.isNull(oauthClient)) {
            throw new OAuth2AuthorizationCodeRequestAuthenticationException(new OAuth2Error("Not found Oauth client by db."), null);
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(clientId)
                .clientId(clientId)
                .clientSecret(oauthClient.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        // 授权模式
        Optional.ofNullable(oauthClient.getAuthorizedGrantTypes())
                        .ifPresent(grants -> StringUtils.commaDelimitedListToSet(grants)
                                .forEach(s -> builder.authorizationGrantType(new AuthorizationGrantType(s))));

        // 回调地址
        Optional.ofNullable(oauthClient.getWebServerRedirectUri()).ifPresent(redirectUri -> Arrays
                .stream(redirectUri.split(StrUtil.COMMA)).filter(StrUtil::isNotBlank).forEach(builder::redirectUri));

        // scope
        Optional.ofNullable(oauthClient.getScope()).ifPresent(
                scope -> Arrays.stream(scope.split(StrUtil.COMMA)).filter(StrUtil::isNotBlank).forEach(builder::scope));

        return builder
                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        .accessTokenTimeToLive(Duration.ofSeconds(Optional
                                .ofNullable(oauthClient.getAccessTokenValidity()).orElse(ACCESS_TOKEN_VALIDITY_SECONDS)))
                        .refreshTokenTimeToLive(
                                Duration.ofSeconds(Optional.ofNullable(oauthClient.getRefreshTokenValidity())
                                        .orElse(REFRESH_TOKEN_VALIDITY_SECONDS)))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(!BooleanUtil.toBoolean(oauthClient.getAutoapprove())).build())
                .build();

    }
}

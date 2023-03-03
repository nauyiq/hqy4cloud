package com.hqy.cloud.auth.server;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.entity.SysOauthClient;
import com.hqy.cloud.auth.service.tk.SysOauthClientTkService;
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
@RequiredArgsConstructor
public class DefaultRegisteredClientRepository implements RegisteredClientRepository {

    private final SysOauthClientTkService sysOauthClientTkService;

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
        SysOauthClient sysOauthClient = sysOauthClientTkService.queryById(clientId);
        if (Objects.isNull(sysOauthClient)) {
            throw new OAuth2AuthorizationCodeRequestAuthenticationException(new OAuth2Error("Not found Oauth client by db."), null);
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(clientId)
                .clientId(clientId)
                .clientSecret(sysOauthClient.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        // 授权模式
        Optional.ofNullable(sysOauthClient.getAuthorizedGrantTypes())
                .ifPresent(grants -> StringUtils.commaDelimitedListToSet(grants)
                        .forEach(s -> builder.authorizationGrantType(new AuthorizationGrantType(s))));

        // 回调地址
        Optional.ofNullable(sysOauthClient.getWebServerRedirectUri()).ifPresent(redirectUri -> Arrays
                .stream(redirectUri.split(StrUtil.COMMA)).filter(StrUtil::isNotBlank).forEach(builder::redirectUri));

        // scope
        Optional.ofNullable(sysOauthClient.getScope()).ifPresent(
                scope -> Arrays.stream(scope.split(StrUtil.COMMA)).filter(StrUtil::isNotBlank).forEach(builder::scope));

        return builder
                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        .accessTokenTimeToLive(Duration.ofSeconds(Optional
                                .ofNullable(sysOauthClient.getAccessTokenValidity()).orElse(ACCESS_TOKEN_VALIDITY_SECONDS)))
                        .refreshTokenTimeToLive(
                                Duration.ofSeconds(Optional.ofNullable(sysOauthClient.getRefreshTokenValidity())
                                        .orElse(REFRESH_TOKEN_VALIDITY_SECONDS)))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(!BooleanUtil.toBoolean(sysOauthClient.getAutoapprove())).build())
                .build();

    }
}

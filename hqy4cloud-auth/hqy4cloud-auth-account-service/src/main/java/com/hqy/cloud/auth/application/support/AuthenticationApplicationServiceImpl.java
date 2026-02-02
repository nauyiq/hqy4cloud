package com.hqy.cloud.auth.application.support;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.hqy.cloud.account.constants.AccountResultCode;
import com.hqy.cloud.account.constants.GrantType;
import com.hqy.cloud.account.request.AuthenticateRequest;
import com.hqy.cloud.account.request.RefreshTokenRequest;
import com.hqy.cloud.account.response.TokenInfo;
import com.hqy.cloud.auth.account.entity.OauthClient;
import com.hqy.cloud.auth.account.service.SysOauthClientDomainService;
import com.hqy.cloud.auth.application.AuthenticationApplicationService;
import com.hqy.cloud.auth.common.AuthException;
import com.hqy.cloud.auth.common.SecurityConstants;
import com.hqy.cloud.auth.oauth.base.Oauth2ResourceOwnerBaseAuthenticationToken;
import com.hqy.cloud.auth.oauth.core.DefaultRegisteredClientRepository;
import com.hqy.cloud.auth.oauth.core.email.Oauth2ResourceOwnerEmailAuthenticationToken;
import com.hqy.cloud.auth.oauth.core.password.Oauth2ResourceOwnerPasswordAuthenticationToken;
import com.hqy.cloud.auth.oauth.core.sms.Oauth2ResourceOwnerSmsAuthenticationToken;
import com.hqy.cloud.auth.security.api.UserDetailsServiceWrapper;
import com.hqy.cloud.auth.security.core.SecurityAuthUser;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author hongqy
 * @date 2026/1/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationApplicationServiceImpl implements AuthenticationApplicationService {

    private final PasswordEncoder passwordEncoder;
    private final SysOauthClientDomainService oauthClientDomainService;
    private final UserDetailsServiceWrapper userDetailsService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final DefaultRegisteredClientRepository registeredClientRepository;
    private final RandomCodeService randomCodeService;
    private final OAuth2AuthorizationService authorizationService;


    @Override
    public TokenInfo authenticate(AuthenticateRequest request) {
        // 1. 验证客户端
        OauthClient oauthClient = validateClient(request.getClientId(), request.getClientSecret());

        // 2. 验证授权类型
        validateGrantType(oauthClient, request.getGrantType());

        // 3. 验证授权范围
        Set<String> authorizedScopes = validateScopes(oauthClient, request.getScopes());

        // 4. 用户认证
        SecurityAuthUser userDetails = authenticateUser(request);

        // 5. 生成Token
        return generateToken(request.getGrantType(), request.getClientSecret(), oauthClient, userDetails, authorizedScopes);
    }


    @Override
    public TokenInfo refreshToken(RefreshTokenRequest request) {
        // 1. 验证客户端
        OauthClient oauthClient = validateClient(request.getClientId(), request.getClientSecret());

        // 2. 验证刷新Token
        OAuth2Authorization authorization = authorizationService.findByToken(request.getRefreshToken(), OAuth2TokenType.REFRESH_TOKEN);
        if (authorization == null) {
            throw new AuthException(ResultCode.INVALID_ACCESS_TOKEN);
        }

        // 3. 获取用户信息
        Object principal = authorization.getAttributes().get(Principal.class.getName());
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        SecurityAuthUser userDetails = (SecurityAuthUser) authenticationToken.getPrincipal();

        // 4. 移除旧Token
        authorizationService.remove(authorization);

        // 5. 生成新Token
        AuthorizationGrantType authorizationGrantType = authorization.getAuthorizationGrantType();
        return generateToken(GrantType.valueOf(authorizationGrantType.getValue().toUpperCase()), request.getClientSecret(),  oauthClient, userDetails, authorization.getAuthorizedScopes());
    }

    @Override
    public boolean revokeToken(String accessToken) {
        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization != null) {
            authorizationService.remove(authorization);
            return true;
        }
        return false;
    }

    /**
     * 生成token
     * @return
     */
    private TokenInfo generateToken(GrantType grantType, String clientSecret, OauthClient oauthClient, SecurityAuthUser userDetails, Set<String> authorizedScopes) {
        // 获取注册客户端
        RegisteredClient registeredClient = registeredClientRepository.getRegisteredClient(oauthClient);

        // 构建客户端认证token
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = new OAuth2ClientAuthenticationToken(
                registeredClient,
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                clientSecret);

        // 构建用户认证Token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 获取授权模式
        AuthorizationGrantType authorizationGrantType = switch (grantType) {
            case SMS -> new AuthorizationGrantType(SecurityConstants.SMS);
            case PASSWORD -> new AuthorizationGrantType(SecurityConstants.PASSWORD);
            case EMAIL -> new AuthorizationGrantType(SecurityConstants.EMAIL);
            default -> throw new UnsupportedOperationException("Unsupported grant type: " + grantType);
        };

        // 获取客户端认证token包装类
        Oauth2ResourceOwnerBaseAuthenticationToken resourceOwnerBaseAuthenticationToken = switch (grantType) {
            case SMS -> new Oauth2ResourceOwnerSmsAuthenticationToken(authorizationGrantType, oAuth2ClientAuthenticationToken, authorizedScopes, Maps.newHashMap());
            case PASSWORD -> new Oauth2ResourceOwnerPasswordAuthenticationToken(authorizationGrantType, oAuth2ClientAuthenticationToken, authorizedScopes, Maps.newHashMap());
            case EMAIL -> new Oauth2ResourceOwnerEmailAuthenticationToken(authorizationGrantType, oAuth2ClientAuthenticationToken, authorizedScopes, Maps.newHashMap());
            default -> throw new UnsupportedOperationException("Unsupported grant type: " + grantType);
        };

        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(authenticationToken)
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(authorizationGrantType)
                .authorizationGrant(resourceOwnerBaseAuthenticationToken);

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient).principalName(userDetails.getUsername())
                .authorizationGrantType(authorizationGrantType)
                // 0.4.0 新增的方法
                .authorizedScopes(authorizedScopes);

        // ----- Access token -----
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", "");
            throw new OAuth2AuthenticationException(error);
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.id(accessToken.getTokenValue())
                    .token(accessToken,
                            (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                                    ((ClaimAccessor) generatedAccessToken).getClaims()))
                    // 0.4.0 新增的方法
                    .authorizedScopes(authorizedScopes)
                    .attribute(Principal.class.getName(), authenticationToken);
        }
        else {
            authorizationBuilder.id(accessToken.getTokenValue()).accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                // Do not issue refresh token to public client
                !oAuth2ClientAuthenticationToken.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the refresh token.", "");
                throw new OAuth2AuthenticationException(error);
            }
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }

        OAuth2Authorization authorization = authorizationBuilder.build();

        this.authorizationService.save(authorization);

        return TokenInfo.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken == null ? StrUtil.EMPTY : refreshToken.getTokenValue())
                .tokenType(OAuth2AccessToken.TokenType.BEARER.getValue())
                .expiresIn(ChronoUnit.SECONDS.between(Instant.now(), accessToken.getExpiresAt()))
                .scopes(authorizedScopes)
                .userId(userDetails.getId())
                .username(userDetails.getUsername())
                .build();
    }

    private SecurityAuthUser authenticateUser(AuthenticateRequest request) {
        GrantType grantType = request.getGrantType();
        String username = request.getAccessAccount();

        // 加载用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Assert.notNull(userDetails, () -> new BizException(AccountResultCode.ACCOUNT_NOT_FOUND));

        SecurityAuthUser securityAuthUser = (SecurityAuthUser) userDetails;
        switch (grantType) {
            case PASSWORD -> Assert.isTrue(passwordEncoder.matches(request.getAccessSecret(), userDetails.getPassword()), () -> new BizException(AccountResultCode.INCORRECT_PASSWORD));
            case SMS -> Assert.isTrue(randomCodeService.isExist(request.getAccessSecret(), request.getAccessAccount(), request.getClientId(), RandomCodeScene.SMS_AUTH), () -> new BizException(AccountResultCode.VERIFY_CODE_ERROR));
            case EMAIL -> Assert.isTrue(randomCodeService.isExist(request.getAccessSecret(), request.getAccessAccount(), request.getClientId(), RandomCodeScene.EMAIL_AUTH), () -> new BizException(AccountResultCode.VERIFY_CODE_ERROR));
        }
        return securityAuthUser;
    }

    /**
     * 验证授权范围
     */
    private Set<String> validateScopes(OauthClient oauthClient, Set<String> requestScopes) {
        String scope = oauthClient.getScope();
        Set<String> allowedScopes = StringUtils.isBlank(scope) ?
                Collections.emptySet() : new HashSet<>(Arrays.asList(scope.split(",")));

        if (CollectionUtils.isEmpty(requestScopes)) {
            return allowedScopes;
        }

        for (String requestScope : requestScopes) {
            if (!SecurityConstants.ALL_GRANT_SCOPE.equals(requestScope)
            && !allowedScopes.contains(requestScope)) {
                throw new BizException(AccountResultCode.UNSUPPORTED_AUTHENTICATION_GRANT_SCOPE);
            }
        }
        return requestScopes;
    }

    /**
     * 验证授权类型
     */
    private void validateGrantType(OauthClient oauthClient, GrantType grantType) {
        String authorizedGrantTypes = oauthClient.getAuthorizedGrantTypes();
        List<GrantType> grantTypes = GrantType.of(authorizedGrantTypes);
        if (StringUtils.isBlank(authorizedGrantTypes) || !grantTypes.contains(grantType)) {
            throw new BizException(AccountResultCode.UNSUPPORTED_AUTHENTICATION_GRANT_TYPE);
        }
    }

    /**
     * 验证客户端
     */
    private OauthClient validateClient(String clientId, String clientSecret) {
        OauthClient oauthClient = oauthClientDomainService.findByClientId(clientId);
        if (oauthClient == null || !oauthClient.getStatus()) {
            throw new BizException(AccountResultCode.AUTH_CLIENT_NOT_EXIST);
        }

        // 验证客户端密钥
        if (!passwordEncoder.matches(clientSecret, oauthClient.getClientSecret())) {
            throw new BizException(AccountResultCode.INVALID_CLIENT_OR_SECRET);
        }
        return oauthClient;
    }

}

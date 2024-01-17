package com.hqy.cloud.auth.core.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ProjectExecutors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.Assert;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @see org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 10:55
 */
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {
    private static final String AUTHORIZATION = "token";
    private final static Long TIMEOUT = 10L;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${hqy4cloud.token.max-size:2}")
    private int maxSize;
    private static final Cache<String, List<String>> ACCESS_TOKEN_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(10240).build();
    private static final Cache<String, List<String>> REFRESH_TOKEN_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).maximumSize(10240).build();

    @Override
    public void save(OAuth2Authorization authorization) {
        if (authorization == null) {
            throw new UnsupportedOperationException("Authorization should not be null.");
        }

        if (isState(authorization)) {
            String token = authorization.getAttribute(OAuth2ParameterNames.STATE);
            if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
                redisTemplate.setValueSerializer(RedisSerializer.java());
            }
            redisTemplate.opsForValue().set(buildKey(OAuth2ParameterNames.STATE, token), authorization, TIMEOUT,
                    TimeUnit.MINUTES);
        }

        if (isCode(authorization)) {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
            OAuth2AuthorizationCode authorizationCodeToken = authorizationCode.getToken();
            long between = ChronoUnit.MINUTES.between(authorizationCodeToken.getIssuedAt(), authorizationCodeToken.getExpiresAt());
            if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
                redisTemplate.setValueSerializer(RedisSerializer.java());
            }
            redisTemplate.opsForValue().set(buildKey(OAuth2ParameterNames.CODE, authorizationCodeToken.getTokenValue()),
                    authorization, between, TimeUnit.MINUTES);
        }

        if (isRefreshToken(authorization)) {
            savingRefreshToken(authorization);
        }

        if (isAccessToken(authorization)) {
            savingAccessToken(authorization);
        }

    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        AssertUtil.notNull(authorization, "authorization should not be null.");

        List<String> keys = new ArrayList<>();
        if (isState(authorization)) {
            String token = authorization.getAttribute(OAuth2ParameterNames.STATE);
            keys.add(buildKey(OAuth2ParameterNames.STATE, token));
        }

        if (isCode(authorization)) {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                    .getToken(OAuth2AuthorizationCode.class);
            OAuth2AuthorizationCode authorizationCodeToken = authorizationCode.getToken();
            keys.add(buildKey(OAuth2ParameterNames.CODE, authorizationCodeToken.getTokenValue()));
        }

        if (isRefreshToken(authorization)) {
            OAuth2RefreshToken refreshToken = authorization.getRefreshToken().getToken();
            keys.add(buildKey(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue()));
        }

        if (isAccessToken(authorization)) {
            OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
            keys.add(buildKey(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getTokenValue()));
        }

        redisTemplate.delete(keys);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        Assert.notNull(tokenType, "tokenType cannot be empty");
        if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
            redisTemplate.setValueSerializer(RedisSerializer.java());
        }
        return (OAuth2Authorization) redisTemplate.opsForValue().get(buildKey(tokenType.getValue(), token));
    }

    private void savingAccessToken(OAuth2Authorization authorization) {
        OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
        String tokenValue = accessToken.getTokenValue();
        long between = ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt());
        if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
            redisTemplate.setValueSerializer(RedisSerializer.java());
        }
        redisTemplate.opsForValue().set(buildKey(OAuth2ParameterNames.ACCESS_TOKEN, tokenValue),
                authorization, between, TimeUnit.SECONDS);

        // 防止单个用户生成太多的TOKEN.
        if (ServerSwitcher.ENABLE_LIMIT_ACCESS_TOKEN_GENERATE_COUNT.isOn()) {
            ProjectExecutors.getInstance().execute(() -> {
                String principalName = authorization.getPrincipalName();
                // 缓存在本地的已经授权过的access_token
                List<String> tokenCaches = ACCESS_TOKEN_CACHE.get(principalName, v -> new ArrayList<>());
                if (CollectionUtils.isNotEmpty(tokenCaches)) {
                    if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
                        redisTemplate.setValueSerializer(RedisSerializer.java());
                    }
                    List<String> keys = tokenCaches.stream().map(token -> buildKey(OAuth2ParameterNames.ACCESS_TOKEN, token)).toList();
                    List<Object> values = redisTemplate.opsForValue().multiGet(keys);
                    if (CollectionUtils.isNotEmpty(values)) {
                        List<OAuth2Authorization> oAuth2Authorizations = values.stream().filter(Objects::nonNull).map(v -> (OAuth2Authorization) v).toList();
                        if (oAuth2Authorizations.size() == maxSize) {
                            // 本地持有的token缓存列表已经达到最大值. 删除最开始认证的OAuth2Authorization
                            String removeToken = tokenCaches.remove(0);
                            redisTemplate.delete(keys.get(0));
                        }
                    }
                }
                tokenCaches.add(tokenValue);
            });
        }
    }

    private void savingRefreshToken(OAuth2Authorization authorization) {
        OAuth2RefreshToken refreshToken = authorization.getRefreshToken().getToken();
        String tokenValue = refreshToken.getTokenValue();
        long between = ChronoUnit.SECONDS.between(refreshToken.getIssuedAt(), refreshToken.getExpiresAt());
        if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
            redisTemplate.setValueSerializer(RedisSerializer.java());
        }
        redisTemplate.opsForValue().set(buildKey(OAuth2ParameterNames.REFRESH_TOKEN, tokenValue),
                authorization, between, TimeUnit.SECONDS);

        // 防止单个用户生成太多的TOKEN.
        if (ServerSwitcher.ENABLE_LIMIT_ACCESS_TOKEN_GENERATE_COUNT.isOn()) {
            ProjectExecutors.getInstance().execute(() -> {
                String principalName = authorization.getPrincipalName();
                // 缓存在本地的已经授权过的refresh_token
                List<String> tokenCaches = REFRESH_TOKEN_CACHE.get(principalName, v -> new ArrayList<>());
                if (CollectionUtils.isNotEmpty(tokenCaches)) {
                    if (CommonSwitcher.ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE.isOff()) {
                        redisTemplate.setValueSerializer(RedisSerializer.java());
                    }
                    List<String> keys = tokenCaches.stream().map(token -> buildKey(OAuth2ParameterNames.REFRESH_TOKEN, token)).toList();
                    List<Object> values = redisTemplate.opsForValue().multiGet(keys);
                    if (CollectionUtils.isNotEmpty(values)) {
                        List<OAuth2Authorization> oAuth2Authorizations = values.stream().filter(Objects::nonNull).map(v -> (OAuth2Authorization) v).toList();
                        if (oAuth2Authorizations.size() == maxSize) {
                            // 本地持有的token缓存列表已经达到最大值. 删除最开始认证的OAuth2Authorization
                            String removeToken = tokenCaches.remove(0);
                            redisTemplate.delete(keys.get(0));
                        }
                    }
                }
                tokenCaches.add(tokenValue);
            });
        }
    }

    private String buildKey(String type, String id) {
        return String.format("%s::%s::%s::%s", AUTHORIZATION, ProjectContext.getContextInfo().getEnv(), type, id);
    }

    private static boolean isCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        return Objects.nonNull(authorizationCode);
    }

    private static boolean isRefreshToken(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getRefreshToken());
    }

    private static boolean isAccessToken(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getAccessToken());
    }

    private static boolean isState(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getAttribute(OAuth2ParameterNames.STATE));
    }
}

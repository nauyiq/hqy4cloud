package com.hqy.cloud.auth.support.core;

import com.hqy.cloud.cache.redis.server.support.RedisManager;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 11:25
 */
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationConsentServiceImpl implements OAuth2AuthorizationConsentService {
    private final static Long TIMEOUT = 10L;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        AssertUtil.notNull(authorizationConsent, "authorizationConsent cannot be null");
        RedisManager.getInstance().set(buildKey(authorizationConsent), authorizationConsent, TIMEOUT, TimeUnit.MINUTES);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        AssertUtil.notNull(authorizationConsent, "authorizationConsent cannot be null");
        RedisManager.getInstance().del(buildKey(authorizationConsent));
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        AssertUtil.notEmpty(registeredClientId, "registeredClientId cannot be empty");
        AssertUtil.notEmpty(principalName, "principalName cannot be empty");
        return RedisManager.getInstance().get(buildKey(registeredClientId, principalName));
    }

    private static String buildKey(OAuth2AuthorizationConsent authorizationConsent) {
        return buildKey(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    private static String buildKey(String registeredClientId, String principalName) {
        return "token:consent:" + registeredClientId + ":" + principalName;
    }

}

package com.hqy.security.server;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.fundation.cache.redis.key.support.DefaultKeyGenerator;
import com.hqy.fundation.cache.redis.support.SmartRedisManager;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/27 17:40
 */
@Component
public class RedisAuthorizationCodeServer extends RandomValueAuthorizationCodeServices {

    public final DefaultKeyGenerator keyGenerator = new DefaultKeyGenerator(MicroServiceConstants.ACCOUNT_SERVICE,
            RedisAuthorizationCodeServer.class.getSimpleName());

    @Override
    protected void store(String code, OAuth2Authentication oAuth2Authentication) {
        SmartRedisManager.getInstance().set(redisKey(code), oAuth2Authentication, 10L, TimeUnit.MINUTES);
    }

    @Override
    protected OAuth2Authentication remove(final String code) {
        String key = redisKey(code);
        OAuth2Authentication oAuth2Authentication = SmartRedisManager.getInstance().get(key, OAuth2Authentication.class);
        SmartRedisManager.getInstance().del(key);
        return oAuth2Authentication;
    }


    private String redisKey(String code) {
        return keyGenerator.genKey(code);
    }
}

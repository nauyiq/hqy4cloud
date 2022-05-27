package com.hqy.auth.server;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.fundation.cache.redis.LettuceRedis;
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


    @Override
    protected void store(String code, OAuth2Authentication oAuth2Authentication) {
        LettuceRedis.getInstance().set(redisKey(code), oAuth2Authentication, 10L, TimeUnit.MINUTES);
    }

    @Override
    protected OAuth2Authentication remove(final String code) {
        String key = redisKey(code);
        OAuth2Authentication oAuth2Authentication = LettuceRedis.getInstance().get(key);
        LettuceRedis.getInstance().del(key);
        return oAuth2Authentication;
    }


    private String redisKey(String code) {
        return RedisAuthorizationCodeServer.class.getSimpleName().concat(BaseStringConstants.Symbol.COLON).concat(code);
    }
}

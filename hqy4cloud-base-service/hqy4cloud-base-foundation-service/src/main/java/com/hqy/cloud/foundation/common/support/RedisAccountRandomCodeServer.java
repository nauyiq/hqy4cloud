package com.hqy.cloud.foundation.common.support;

import cn.hutool.core.util.RandomUtil;
import com.hqy.cloud.foundation.cache.redis.support.SmartRedisManager;
import com.hqy.cloud.foundation.cache.redis.key.support.DefaultKeyGenerator;
import com.hqy.cloud.foundation.common.AccountRandomCodeServer;
import com.hqy.cloud.util.AssertUtil;

import java.util.concurrent.TimeUnit;

/**
 * RedisRandomCodeServer.
 * @see AccountRandomCodeServer
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 17:10
 */
public abstract class RedisAccountRandomCodeServer implements AccountRandomCodeServer {

    private final DefaultKeyGenerator keyGenerator;
    private int maxRetry = 5;

    public RedisAccountRandomCodeServer(DefaultKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Override
    public String randomCode(String username, String email) {
        return this.randomCode(username, email, DEFAULT_LENGTH, DEFAULT_SECONDS);
    }

    @Override
    public String randomCode(String username, String email, int length) {
        return this.randomCode(username, email, length, DEFAULT_SECONDS);
    }

    @Override
    public String randomCode(String username, String email, int length, int expiredSeconds) {
        String code = RandomUtil.randomString(length);
        int i = 1;
        while (!SmartRedisManager.getInstance().set(keyGenerator.genKey(email+ code + username), "1", (long)expiredSeconds, TimeUnit.SECONDS)) {
            AssertUtil.isTrue(i <= maxRetry, "Already generator random code max time.");
            code = RandomUtil.randomString(length);
            i++;
        }
        return code;
    }

    @Override
    public boolean isExist(String username, String email, String code) {
        return SmartRedisManager.getInstance().exists(keyGenerator.genKey(email+ code + username));
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }
}

package com.hqy.cloud.foundation.common.support;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import com.hqy.cloud.foundation.cache.redis.support.SmartRedisManager;
import com.hqy.cloud.foundation.common.account.AccountRandomCodeServer;
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

    private final RedisKey redisKey;
    private int maxRetry = 5;

    public RedisAccountRandomCodeServer(RedisKey redisKey) {
        this.redisKey = redisKey;
    }

    @Override
    public String randomCode(String usernameOrEmail) {
        boolean isEmail = Validator.isEmail(usernameOrEmail);
        return isEmail ? this.randomCode(StrUtil.EMPTY, usernameOrEmail) : this.randomCode(usernameOrEmail, StrUtil.EMPTY);
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
        while (!SmartRedisManager.getInstance().set(redisKey.getKey(email+ code + username), "1", (long)expiredSeconds, TimeUnit.SECONDS)) {
            AssertUtil.isTrue(i <= maxRetry, "Already generator random code max time.");
            code = RandomUtil.randomString(length);
            i++;
        }
        return code;
    }

    @Override
    public boolean isExist(String username, String email, String code) {
        return SmartRedisManager.getInstance().exists(redisKey.getKey(email+ code + username));
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }
}

package com.hqy.cloud.infrastructure.random;

import cn.hutool.core.util.RandomUtil;
import com.hqy.cloud.cache.common.RedisConstants;
import com.hqy.cloud.cache.common.RedisException;
import com.hqy.cloud.cache.redis.server.support.SmartRedisManager;
import com.hqy.cloud.common.base.lang.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/2
 */
@Slf4j
public class RedisRandomCodeService implements RandomCodeService {
    private static final String PREFIX = "random:";

    @Override
    public String randomNumber(int length, int expired, TimeUnit timeUnit, String... params) {
        Assert.isTrue(length > 0, "length must be greater than 0");
        // 生成随机的code
        String code = RandomUtil.randomNumbers(length);
        return doRandom(expired, timeUnit, params, code);
    }

    @Override
    public String randomStr(int length, int expired, TimeUnit timeUnit, String... params) {
        Assert.isTrue(length > 0, "length must be greater than 0");
        // 生成随机的code
        String code = RandomUtil.randomString(length);
        return doRandom(expired, timeUnit, params, code);
    }

    private String doRandom(long expired, TimeUnit timeUnit, String[] params, String code) {
        String redisKey = getRedisKey(code, params);
        Boolean set = SmartRedisManager.getInstance().set(redisKey, StringConstants.EMPTY, expired, timeUnit);
        if (Boolean.TRUE.equals(set)) {
            return code;
        }
        log.error("Failed execute to set redisKey:{}", redisKey);
        throw new RedisException("Failed execute to set redisKey:" + redisKey);
    }

    @Override
    public boolean isExist(String code, String... params) {
        String redisKey = getRedisKey(code, params);
        Boolean exists = SmartRedisManager.getInstance().exists(redisKey);
        return Boolean.TRUE.equals(exists);
    }
    
    private String getRedisKey(String code, String... params){
        Assert.notEmpty(params, "Request params should not be empty.");
        String suffix = Arrays.stream(params).reduce((a, b) -> a + RedisConstants.CACHE_KEY_SEPARATOR + b).orElse(StringConstants.EMPTY);
        return PREFIX + suffix + RedisConstants.CACHE_KEY_SEPARATOR + code;
    }
    


}

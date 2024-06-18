package com.hqy.cloud.infrastructure.random;

import cn.hutool.core.util.RandomUtil;
import com.hqy.cloud.cache.common.RedisConstants;
import com.hqy.cloud.cache.common.RedisException;
import com.hqy.cloud.cache.redis.server.support.SmartRedisManager;
import com.hqy.cloud.common.base.lang.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public String randomNumber(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        Assert.isTrue(length > 0, "length must be greater than 0");
        // 生成随机的code
        String code = RandomUtil.randomNumbers(length);
        return doRandom(expired, timeUnit, scene, code, value);
    }

    @Override
    public String randomStr(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        Assert.isTrue(length > 0, "length must be greater than 0");
        // 生成随机的code
        String code = RandomUtil.randomString(length);
        return doRandom(expired, timeUnit, scene, code, value);
    }

    private String doRandom(long expired, TimeUnit timeUnit, RandomCodeScene scene, String code, String value) {
        String redisKey = getRedisKey(code, scene.suffix);
        Boolean set = SmartRedisManager.getInstance().set(redisKey, value, expired, timeUnit);
        if (Boolean.TRUE.equals(set)) {
            return code;
        }
        log.error("Failed execute to set redisKey:{}", redisKey);
        throw new RedisException("Failed execute to set redisKey:" + redisKey);
    }

    @Override
    public boolean isExist(String code, String value, RandomCodeScene scene) {
        String redisKey = getRedisKey(code, scene.suffix);
        String data = SmartRedisManager.getInstance().get(redisKey);
        return StringUtils.isNotBlank(data) && data.equals(value);
    }

    @Override
    public boolean saveCode(String code, String value, RandomCodeScene scene) {
        String redisKey = getRedisKey(code, scene.suffix);
        String data = SmartRedisManager.getInstance().get(redisKey);
        Boolean set = SmartRedisManager.getInstance().set(redisKey, value, 5L, TimeUnit.MINUTES);
        return Boolean.TRUE.equals(set);
    }

    private String getRedisKey(String code, String suffix){
        return PREFIX + suffix + code;
    }
    


}

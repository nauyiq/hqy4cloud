package com.hqy.cloud.gateway.server;

import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.redis.key.RedisKey;
import com.hqy.cloud.foundation.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.redis.support.SmartRedisManager;
import org.apache.commons.lang3.StringUtils;

/**
 * AbstractImageCodeServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/7 10:23
 */
public abstract class AbstractCodeServer {

    public static final String RANDOM_KEY = "randomStr";
    protected final static RedisKey KEY = new RedisNamedKey(MicroServiceConstants.GATEWAY, RANDOM_KEY);

    protected void saveCode(String key, String code) {
        if (StringUtils.isBlank(code)) {
            return;
        }
        SmartRedisManager.getInstance().set(KEY.getKey(key), code, NumberConstants.FIVE_MINUTES_4MILLISECONDS);
    }

    protected String getCode(String key) {
        String redisKey = KEY.getKey(key);
        return SmartRedisManager.getInstance().get(redisKey);
    }






}

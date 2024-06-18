package com.hqy.cloud.gateway.server;

import com.hqy.cloud.cache.common.RedisKeyUtil;
import com.hqy.cloud.cache.redis.server.support.SmartRedisManager;
import com.hqy.cloud.common.base.lang.NumberConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * AbstractImageCodeServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/7 10:23
 */
public abstract class AbstractCodeServer {

    public static final String RANDOM_KEY = "randomStr";

    protected void saveCode(String key, String code) {
        if (StringUtils.isBlank(code)) {
            return;
        }
        String cacheKey = RedisKeyUtil.getCacheKey("gateway-code", key);
        SmartRedisManager.getInstance().set(cacheKey, code, NumberConstants.FIVE_MINUTES_4MILLISECONDS);
    }

    protected String getCode(String key) {
        String cacheKey = RedisKeyUtil.getCacheKey("gateway-code", key);
        return SmartRedisManager.getInstance().get(cacheKey);
    }






}

package com.hqy.gateway.server;

import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.fundation.cache.redis.key.support.DefaultKeyGenerator;
import com.hqy.fundation.cache.redis.support.SmartRedisManager;
import org.apache.commons.lang3.StringUtils;

/**
 * AbstractImageCodeServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/7 10:23
 */
public abstract class AbstractCodeServer {

    public static final String RANDOM_KEY = "randomStr";
    protected final static DefaultKeyGenerator KEY_GENERATOR = new DefaultKeyGenerator(MicroServiceConstants.GATEWAY, RANDOM_KEY);

    protected void saveCode(String key, String code) {
        if (StringUtils.isBlank(code)) {
            return;
        }
        SmartRedisManager.getInstance().set(KEY_GENERATOR.genKey(key), code, BaseMathConstants.FIVE_MINUTES_4MILLISECONDS);
    }

    protected String getCode(String key) {
        String redisKey = KEY_GENERATOR.genKey(key);
        return SmartRedisManager.getInstance().get(redisKey);
    }






}

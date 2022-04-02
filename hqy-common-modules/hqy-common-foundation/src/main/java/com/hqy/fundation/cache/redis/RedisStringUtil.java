package com.hqy.fundation.cache.redis;

import com.hqy.base.common.swticher.CommonSwitcher;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 17:59
 */
public class RedisStringUtil {

    public static AbstractRedisAdaptor instance() {
        if (CommonSwitcher.ENABLE_LETTUCE_REDIS_TEMPLATE.isOn()) {
            return LettuceStringRedis.getInstance();
        } else {
            return JedisRedis.getInstance();
        }
    }

}

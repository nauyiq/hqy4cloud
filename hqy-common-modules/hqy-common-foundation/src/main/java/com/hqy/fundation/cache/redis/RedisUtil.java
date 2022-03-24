package com.hqy.fundation.cache.redis;

import com.hqy.base.common.swticher.CommonSwitcher;

/**
 * 根据节点快关选用那种redis客户端
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/3 11:09
 */
public class RedisUtil {

    public static AbstractRedisTemplateUtil instance() {
        if (CommonSwitcher.ENABLE_LETTUCE_REDIS_TEMPLATE.isOn()) {
            return LettuceRedis.getInstance();
        } else {
            return JedisRedis.getInstance();
        }
     }


}

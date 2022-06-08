package com.hqy.access.flow.strategy.counter;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.foundation.cache.CacheKey;
import com.hqy.fundation.cache.redis.LettuceStringRedis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 17:28
 */
@Slf4j
public class RedisCounterLimiter extends AbstractCounterLimiter {

    private final CacheKey redisKey;

    public RedisCounterLimiter(long count) {
        super(count);
        redisKey = new CacheKey(Integer.MAX_VALUE, RedisCounterLimiter.class.getSimpleName().concat(BaseStringConstants.Symbol.COLON)) {};
    }

    @Override
    protected long currentCount() {
        String count =  LettuceStringRedis.getInstance().get(redisKey.key());
        return StringUtils.isBlank(count) ? 0 : Long.parseLong(count);
    }

    @Override
    public void increment() {
        LettuceStringRedis.getInstance().incr(redisKey.key(), 1);
    }

    @Override
    public void decrement() {
        LettuceStringRedis.getInstance().incr(redisKey.key(), -1);
    }
}

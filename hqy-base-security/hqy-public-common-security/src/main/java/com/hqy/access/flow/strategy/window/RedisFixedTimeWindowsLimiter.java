package com.hqy.access.flow.strategy.window;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.foundation.cache.CacheKey;
import com.hqy.fundation.cache.redis.LettuceStringRedis;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于redis的固定窗口限流器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/7 13:31
 */
@Slf4j
public class RedisFixedTimeWindowsLimiter extends AbstractTimeWindowsLimiter {

    private final CacheKey cacheKey;

    public RedisFixedTimeWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
        cacheKey = new CacheKey(RedisFixedTimeWindowsLimiter.class.getSimpleName() + BaseStringConstants.Symbol.COLON, (int) (getSecondWindows() + 1)) {};
    }


    @Override
    protected long currentCount() {
        long currentSeconds = currentTimeMillis() / 1000;
        return LettuceStringRedis.getInstance().incr(cacheKey.key() + currentSeconds, 1);
    }

}

package com.hqy.access.flow.strategy.window;

import com.alibaba.csp.sentinel.util.TimeUtil;
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
public class RedisFixedWindowsLimiter extends AbstractWindowLimiter {

    private final CacheKey cacheKey;

    public RedisFixedWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
        cacheKey = new CacheKey((int) (getSecondWindows() + 1), RedisFixedWindowsLimiter.class.getSimpleName() + BaseStringConstants.Symbol.COLON) {};
    }


    @Override
    protected long currentCount() {
        long currentSeconds = TimeUtil.currentTimeMillis() / 1000;
        return LettuceStringRedis.getInstance().incr(cacheKey.key() + currentSeconds, 1);
    }

}

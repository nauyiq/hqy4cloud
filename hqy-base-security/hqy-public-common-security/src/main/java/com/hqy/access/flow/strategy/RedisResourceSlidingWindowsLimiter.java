package com.hqy.access.flow.strategy;

import com.hqy.access.flow.FlowLimitConfig;
import com.hqy.access.flow.LimitMode;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.fundation.cache.redis.LettuceStringRedis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

/**
 * redis 滑动窗口限流器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/17 14:36
 */
@Slf4j
public class RedisResourceSlidingWindowsLimiter extends AbstractLimiter {

    public RedisResourceSlidingWindowsLimiter(FlowLimitConfig config) {
        super(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isOverLimit(String resource) {
        //流量限制规则
        FlowLimitConfig config = getConfig();
        LimitMode limitMode = config.getLimitMode();
        if (limitMode == LimitMode.THREAD_COUNT) {
            resource = RedisResourceSlidingWindowsLimiter.class.getSimpleName();
        }

        //当前时间窗口
        long now = currentTimeMillis();
        long timeWindow = config.getWindows().seconds * BaseMathConstants.ONE_SECONDS_4MILLISECONDS;

        Long flag = null;
        try {
            //lua脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setResultType(Long.class);
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit/slidingLimit.lua")));
            flag = (Long) LettuceStringRedis.getInstance().getRedisTemplate().execute(redisScript, Collections.singletonList(resource),
                    String.valueOf(now - timeWindow), String.valueOf(now), String.valueOf(config.getCount()), String.valueOf(now), String.valueOf(config.getBlockSeconds()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (flag == null) {
            return false;
        }
        return flag != 1;
    }
}

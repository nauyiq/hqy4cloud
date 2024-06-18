package com.hqy.cloud.limiter.core;

import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.cache.redis.server.support.SmartRedisManager;
import com.hqy.cloud.limiter.api.AbstractLimiter;
import com.hqy.cloud.limiter.flow.FlowLimitConfig;
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
    public boolean isOverLimit(String resource) {
        //流量限制规则
        FlowLimitConfig config = getConfig();
        LimitMode limitMode = config.getLimitMode();
        if (limitMode == LimitMode.THREAD_COUNT) {
            resource = RedisResourceSlidingWindowsLimiter.class.getSimpleName();
        }

        //当前时间窗口
        long now = currentTimeMillis();
        long timeWindow = config.getWindows() * NumberConstants.ONE_SECONDS_4MILLISECONDS;

        Long flag = null;
        try {
            //lua脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setResultType(Long.class);
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit/slidingLimit.lua")));
            flag = SmartRedisManager.getInstance().getRedisTemplate().execute(redisScript, Collections.singletonList(resource),
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

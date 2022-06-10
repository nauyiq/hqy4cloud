package com.hqy.access.flow.strategy.window;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.foundation.cache.CacheKey;
import com.hqy.fundation.cache.redis.LettuceStringRedis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 基于redis zset 滑动窗口限流器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/8 16:57
 */
@Slf4j
public class RedisSlidingTimeWindowsLimiter extends AbstractTimeWindowsLimiter {

    private final CacheKey cacheKey = new CacheKey(RedisSlidingTimeWindowsLimiter.class.getSimpleName().concat(BaseStringConstants.Symbol.COLON));

    public RedisSlidingTimeWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected long currentCount() {
        //获取当前系统时间.
        long time = currentTimeMillis();

        RedisTemplate redisTemplate = LettuceStringRedis.getInstance().getRedisTemplate();
        List<String> pipelined = redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            connection.openPipeline();
            connection.multi();
            //添加当前时间窗口下的用户 值就为当前时间戳
            connection.zAdd(cacheKey.key().getBytes(StandardCharsets.UTF_8), time, String.valueOf(time).getBytes(StandardCharsets.UTF_8));
            //移除当前窗口之外的数据
            connection.zRemRangeByScore(cacheKey.key().getBytes(StandardCharsets.UTF_8), 0, time - (secondWindows * 1000));
            //计算数目即为当前窗口访问量
            Long card = connection.zCard(cacheKey.key().getBytes(StandardCharsets.UTF_8));
            log.info("当前窗口 {} 访问量 {} ", time, card);
            connection.exec();
            connection.closePipeline();
            return null;
        });

        if (CollectionUtils.isNotEmpty(pipelined)) {
            return Long.parseLong(pipelined.get(pipelined.size() - 1));
        }

        return 0;
    }
}

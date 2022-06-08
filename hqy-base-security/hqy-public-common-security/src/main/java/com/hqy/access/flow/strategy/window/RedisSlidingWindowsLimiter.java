package com.hqy.access.flow.strategy.window;

import com.hqy.fundation.cache.redis.LettuceStringRedis;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
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
public class RedisSlidingWindowsLimiter extends AbstractWindowLimiter {

    public RedisSlidingWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
    }

    @Override
    protected long currentCount() {

        String key = "test";

        long timeMillis = System.currentTimeMillis();

        RedisTemplate redisTemplate = LettuceStringRedis.getInstance().getRedisTemplate();
        List list = redisTemplate.executePipelined(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.multi();
                connection.zAdd(key.getBytes(StandardCharsets.UTF_8), timeMillis, String.valueOf(timeMillis).getBytes(StandardCharsets.UTF_8));
                connection.zRemRangeByScore(key.getBytes(StandardCharsets.UTF_8), 0, timeMillis - secondWindows * 1000);
                Long card = connection.zCard(key.getBytes(StandardCharsets.UTF_8));
                connection.exec();
                return card;
            }
        });



        return 0;
    }
}

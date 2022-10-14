package com.hqy.fundation.cache.redis;

import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * 基于Jedis缓存工具类
 * @author qy
 * @date  2021-07-22 16:10
 */
@Slf4j
public class JedisRedis extends AbstractRedisAdaptor {

    private JedisRedis(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    private static volatile JedisRedis instance = null;

    public static JedisRedis getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (LettuceRedis.class) {
                if (Objects.isNull(instance)) {
                    @SuppressWarnings("unchecked")
                    RedisTemplate<String, Object> template = SpringContextHolder.getBean(RedisTemplate.class, "JedisRedisTemplate");
                    instance = new JedisRedis(template);
                }
            }
        }
        return instance;
    }



}

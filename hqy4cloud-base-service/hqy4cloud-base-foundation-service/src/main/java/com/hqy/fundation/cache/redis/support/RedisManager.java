package com.hqy.fundation.cache.redis.support;

import com.hqy.fundation.cache.redis.AbstractRedisAdaptor;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * RedisManager.
 * 基于RedisTemplate
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/26 15:15
 */
@Slf4j
public class RedisManager extends AbstractRedisAdaptor {

    private RedisManager(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    private static volatile RedisManager instance = null;

    @SuppressWarnings("unchecked")
    public static RedisManager getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (RedisManager.class) {
                if (Objects.isNull(instance)) {
                    instance = new RedisManager(SpringContextHolder.getBean(RedisTemplate.class, "redisTemplate"));
                }
            }
        }
        return instance;
    }

}

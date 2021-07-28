package com.hqy.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

/**
 * 基于redisTemplate的缓存工具类
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-23 11:26
 */
@Slf4j
public abstract class AbstractRedisTemplateUtil {

    public AbstractRedisTemplateUtil(){}

    private RedisTemplate<String, Serializable> redisTemplate;


    /**
     * 删除RedisTemplate中配置的db的所有key
     */
    public void flushDB() {
        redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.flushDb();
            return "ok";
        });
    }





}

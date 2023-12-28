package com.hqy.cloud.foundation.redis;

import com.hqy.foundation.cache.redis.Redis;
import com.hqy.cloud.foundation.redis.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于redisTemplate的缓存工具类
 * 父类构造需要传入对应的redisTemplate模板
 * @author qy
 * @date  2021-07-23 11:26
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class AbstractRedisAdaptor extends DefaultRedisOperations implements Redis {

    public AbstractRedisAdaptor(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }


    @Override
    public <T> T get(String key) {
        try {
            return (T) getRedisTemplate().opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed execute to redis [get]. RedisKey: {}.", key, e);
            return null;
        }
    }

    @Override
    public void hSet(String key, Object hashKey, Object value) {
        try {
            getRedisTemplate().opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Failed execute to redis [hSet]. RedisKey: {}.", key, e);
        }
    }

    @Override
    public void hSet(String key, Object hashKey, Object value, long time, TimeUnit timeUnit) {
        try {
            getRedisTemplate().opsForHash().put(key, hashKey, value);
            expire(key, time, timeUnit);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [hSet]. Key: " + key, e);
        }
    }

    @Override
    public void hmSet(String key, Map<Object, Object> data) {
        try {
            getRedisTemplate().opsForHash().putAll(key, data);
        } catch (Exception e) {
            log.error("Failed execute to redis [hmSet]. RedisKey: {}.", key, e);
        }
    }

    @Override
    public <T> T hGet(String key, Object hashKey) {
       try {
           return (T) getRedisTemplate().opsForHash().get(key, hashKey);
       } catch (Exception e) {
           log.error("Failed execute to redis [hGet]. RedisKey: {}.", key, e);
           return null;
       }
    }


    @Override
    public <T> List<T> hmGet(String key, List<Object> hashKeys) {
        try {
            return (List<T>) getRedisTemplate().opsForHash().multiGet(key, hashKeys);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [hmGet]. Key: " + key, e);
        }
    }


    @Override
    public <K, V> Map<K, V> hGetAll(String key) {
        try {
            return (Map<K, V>) getRedisTemplate().opsForHash().entries(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [hGet]. Key: " + key, e);
        }
   }


    @Override
    public <T> Set<T> sMembers(String key) {
        try {
            return (Set<T>)getRedisTemplate().opsForSet().members(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [sMembers]. key: " + key, e);
        }
    }

    @Override
    public <T> T lPop(String key) {
        try {
            return (T) getRedisTemplate().opsForList().leftPop(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lPop]. Key: " + key, e);
        }
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end) {
        try {
            return (List<T>)getRedisTemplate().opsForList().range(key, start, end);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lRange]. Key: " + key, e);
        }
    }


    @Override
    public <T> T rPop(String key) {
        try {
            return (T) getRedisTemplate().opsForList().rightPop(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [rPop]. Key: " + key, e);
        }
    }


}

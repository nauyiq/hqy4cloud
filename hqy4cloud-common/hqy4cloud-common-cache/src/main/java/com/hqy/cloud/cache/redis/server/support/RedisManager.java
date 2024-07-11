package com.hqy.cloud.cache.redis.server.support;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.cache.common.RedisConstants;
import com.hqy.cloud.cache.common.RedisException;
import com.hqy.cloud.cache.redis.server.AbstractTemplateOperations;
import com.hqy.cloud.cache.redis.server.RedisObjectTemplateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisManager.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@SuppressWarnings("unchecked")
public class RedisManager extends AbstractTemplateOperations implements RedisObjectTemplateOperations {
   private static final Logger log = LoggerFactory.getLogger(RedisManager.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisManager(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 单例
     */
    private static volatile RedisManager instance = null;

    public static RedisManager getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (RedisManager.class) {
                if (Objects.isNull(instance)) {
                    instance = new RedisManager(SpringUtil.getBean(RedisTemplate.class));
                }
            }
        }
        return instance;
    }



    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
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

package com.hqy.cloud.foundation.cache.redis;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.foundation.cache.redis.StringRedis;
import com.hqy.cloud.foundation.cache.exception.RedisException;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于redisTemplate的缓存工具类
 * 父类构造需要传入对应的redisTemplate模板
 * @author qy
 * @date  2021-07-23 11:26
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractStringRedisAdaptor extends DefaultRedisOperations implements StringRedis {

    private final StringRedisTemplate redisTemplate;


    public AbstractStringRedisAdaptor(StringRedisTemplate redisTemplate) {
        super((RedisTemplate) redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Throwable cause) {
            log.error("Failed execute to redis [get]. RedisKey: {}.", key, cause);
            return StringConstants.EMPTY;
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            return StringUtils.isBlank(json) ? null : JsonUtil.toBean(json, clazz);
        } catch (Throwable cause) {
            log.error("Failed execute to redis [get]. RedisKey: {}.", key, cause);
            return null;
        }
    }

    @Override
    public Boolean set(String key, Object value, Long time, TimeUnit timeUnit) {
        boolean setEx = time != null && time > 0L;
        try {
            String newValue = value instanceof String ? (String) value : JsonUtil.toJson(value);
            if (setEx) {
                redisTemplate.opsForValue().set(key, newValue, time, timeUnit);
            } else {
                redisTemplate.opsForValue().set(key, newValue);
            }
        }  catch (Exception e) {
            log.error("Failed execute to redis [set]. RedisKey: {}.", key, e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean setEx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value instanceof String ? (String) value : JsonUtil.toJson(value), time, timeUnit);
        } catch (Exception e) {
            log.error("Failed execute to redis [setEx]. RedisKey: {}.", key, e);
            return false;
        }
    }

    @Override
    public Boolean setNx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfPresent(key, value instanceof String ? (String) value : JsonUtil.toJson(value), time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed execute to redis [setNx]. RedisKey: {}.", key, e);
            return false;
        }
    }


    @Override
    public String hGet(String key, String hashField) {
        try {
            return (String) redisTemplate.opsForHash().get(key, hashField);
        } catch (Throwable cause) {
            log.error("Failed execute to redis [hGet]. RedisKey: {}.", key, cause);
            return StringConstants.EMPTY;
        }
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value instanceof String ? value : JsonUtil.toJson(value));
        } catch (Exception e) {
            log.error("Failed execute to redis [hSet]. RedisKey: {}.", key, e);
        }
    }


    @Override
    public void hSet(String key, String hashKey, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value instanceof String ? value : JsonUtil.toJson(value));
            expire(key, time, timeUnit);
        } catch (Exception e) {
            log.error("Failed execute to redis [hSet]. RedisKey: {}.", key, e);
        }
    }

    @Override
    public void hmSet(String key, Map<String, Object> data) {
        try {
            Map<String, String> stringStringMap = data.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
                Object value = e.getValue();
                return value instanceof String ? (String) value : JsonUtil.toJson(value);
            }));
            redisTemplate.opsForHash().putAll(key, stringStringMap);
        } catch (Exception e) {
            log.error("Failed execute to redis [hmSet]. RedisKey: {}.", key, e);
        }
    }

    @Override
    public <T> T hGet(String key, String hashField, Class<T> clazz) {
        try {
            String json = (String) redisTemplate.opsForHash().get(key, hashField);
            return StringUtils.isBlank(json) ? null : JsonUtil.toBean(json, clazz);
        } catch (Throwable cause) {
            log.error("Failed execute to redis [hGet]. RedisKey: {}.", key, cause);
            return null;
        }
    }


    @Override
    public List<String> hmGet(String key, Collection<Object> hashKeys) {
        try {
            List<Object> objects = redisTemplate.opsForHash().multiGet(key, hashKeys);
            if (CollectionUtils.isEmpty(objects)) {
                return Collections.emptyList();
            }
            return objects.parallelStream().map(Objects::toString).collect(Collectors.toList());
        } catch (Throwable cause) {
            log.error("Failed execute to redis [hmGet]. RedisKey: {}.", key, cause);
            return Collections.emptyList();
        }
    }

    @Override
    public <T> List<T> hmGet(String key, Collection<Object> hashKeys, Class<T> clazz) {
        try {
            List<Object> objects = redisTemplate.opsForHash().multiGet(key, hashKeys);
            if (CollectionUtils.isEmpty(objects)) {
                return Collections.emptyList();
            }
            return objects.parallelStream().map(e -> {
                String json = e.toString();
                return JsonUtil.toBean(json, clazz);
            }).collect(Collectors.toList());
        } catch (Throwable cause) {
            log.error("Failed execute to redis [hmGet]. RedisKey: {}.", key, cause);
            return Collections.emptyList();
        }

    }

    @Override
    public Map<String, String> hGetAll(String key) {
        try {
            Map map = redisTemplate.opsForHash().entries(key);
            return (Map<String, String>) map;
        } catch (Exception e) {
            log.error("Failed execute to redis [hGetAll]. RedisKey: {}.", key, e);
            return null;
        }
    }

    @Override
    public <T> Map<String, T> hGetAll(String key, Class<T> clazz) {
        try {
            Map map = redisTemplate.opsForHash().entries(key);
            return  ((Map<String, String>) map).entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        String json = e.getValue();
                        return JsonUtil.toBean(json, clazz);
                    }));

        } catch (Exception e) {
            log.error("Failed execute to redis [hGetAll]. RedisKey: {}.", key, e);
            return null;
        }

    }

    @Override
    public Long sAdd(String key, Object... values) {
        try {
            String[] strings = Arrays.stream(values).map(value -> value instanceof String ? (String) value : JsonUtil.toJson(value)).toArray(String[]::new);
            return redisTemplate.opsForSet().add(key, strings);
        } catch (Exception e) {
            log.error("Failed execute to redis [sAdd]. RedisKey: {}.", key, e);
            return 0L;
        }
    }

    @Override
    public Long sAdd(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            String[] strings = Arrays.stream(values).map(value -> value instanceof String ? (String) value : JsonUtil.toJson(value)).toArray(String[]::new);
            Long count = redisTemplate.opsForSet().add(key, strings);
            expire(key, time, timeUnit);
            return count;
        } catch (Exception e) {
            log.error("Failed execute to redis [sAdd]. RedisKey: {}.", key, e);
            return 0L;
        }
    }


    @Override
    public Set<String> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Failed execute to redis [sMembers]. RedisKey: {}.", key, e);
            return null;
        }

    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value instanceof String ? value : JsonUtil.toJson(value));
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [sIsMember]. key: " + key, e);
        }
    }

    @Override
    public <T> Set<T> sMembers(String key, Class<T> clazz) {
        try {
            Set<String> members = redisTemplate.opsForSet().members(key);
            if (CollectionUtils.isNotEmpty(members)) {
                return members.parallelStream().map(e -> JsonUtil.toBean(e, clazz)).collect(Collectors.toSet());
            } else {
                return Collections.emptySet();
            }
        } catch (Exception e) {
            log.error("Failed execute to redis [sMembers]. RedisKey: {}.", key, e);
            return null;
        }
    }


    @Override
    public Long sRem(String key, Object... values) {
        try {
            Object[] strings = Arrays.stream(values).map(value -> value instanceof String ? (String) value : JsonUtil.toJson(value)).toArray();
            return redisTemplate.opsForSet().remove(key, strings);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [sRem]. key: " + key, e);
        }
    }

    @Override
    public Boolean zADD(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value instanceof String ? (String) value: JsonUtil.toJson(value), score);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [zADD]. key: " + key, e);
        }
    }

    @Override
    public Long zRem(String key, Object... values) {
        try {
            Object[] strings = Arrays.stream(values).map(value -> value instanceof String ? (String) value : JsonUtil.toJson(value)).toArray();
            return redisTemplate.opsForZSet().remove(key, strings);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [zRem].  key: " + key, e);
        }
    }

    @Override
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value instanceof String ? value : JsonUtil.toJson(value));
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [zRank]. key: " + key, e);
        }
    }

    @Override
    public Long lPush(String key, Object... values) {
        try {
            String[] strings = Arrays.stream(values).map(value -> value instanceof String ? (String) value : JsonUtil.toJson(value)).toArray(String[]::new);
            if (values.length > 1) {
                return redisTemplate.opsForList().leftPushAll(key, strings);
            } else {
                return redisTemplate.opsForList().leftPush(key, strings[0]);
            }
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lPush]. Key: " + key, e);
        }
    }

    @Override
    public String lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lPush]. Key: " + key, e);
        }
    }

    @Override
    public <T> T lPop(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForList().leftPop(key);
            return StringUtils.isBlank(json) ? null : JsonUtil.toBean(json, clazz);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lPush]. Key: " + key, e);
        }
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lPush]. Key: " + key, e);
        }
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        try {
            List<String> range = redisTemplate.opsForList().range(key, start, end);
            if (CollectionUtils.isEmpty(range)) {
                return Collections.emptyList();
            }
            return range.stream().map(json -> JsonUtil.toBean(json, clazz)).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [lPush]. Key: " + key, e);
        }
    }

    @Override
    public Long rPush(String key, Object... values) {
        try {
            String[] strings = Arrays.stream(values).map(value -> value instanceof String ? (String) value : JsonUtil.toJson(value)).toArray(String[]::new);
            if (values.length > 1) {
                return redisTemplate.opsForList().rightPushAll(key, strings);
            } else {
                return redisTemplate.opsForList().rightPush(key, strings[0]);
            }
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [rPush]. Key: " + key, e);
        }
    }


    @Override
    public String rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [rPop]. Key: " + key, e);
        }
    }

    @Override
    public <T> T rPop(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForList().rightPop(key);
            return StringUtils.isBlank(json) ? null : JsonUtil.toBean(json, clazz);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [rPop]. Key: " + key, e);
        }
    }
}

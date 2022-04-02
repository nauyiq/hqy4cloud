package com.hqy.fundation.cache.redis;

import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.foundation.cache.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 基于redisTemplate的缓存工具类
 * 父类构造需要传入对应的redisTemplate模板
 * @author qy
 * @date  2021-07-23 11:26
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractRedisAdaptor implements RedisService {

    protected AbstractRedisAdaptor(RedisTemplate redisTemplate) {
        AbstractRedisAdaptor.redisTemplate = redisTemplate;
    }

    /**
     * 由子类进行构造，由子类选择构造的redis客户端，是lettuce还是jedis
     */
    private static RedisTemplate redisTemplate;


    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }


    /**
     * 选择需要操作的redis db
     * @param db redis db下标 注意的是只能是0-15
     * @return RedisService
     */
    public abstract AbstractRedisAdaptor selectDb(int db);

    @Override
    public void flushDb() {
        try {
            redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
                redisConnection.flushDb();
                return "ok";
            });
        } catch (Exception e) {
            log.error("@@@ [flushDb] failure: ", e);
        }
    }

    @Override
    public void expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            log.error("@@@ [expire] failure: ", e);
        }
    }


    public Long ttl(String key) {
        return ttl(key, TimeUnit.SECONDS);
    }

    @Override
    public Long ttl(String key, TimeUnit timeUnit) {
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (Exception e) {
            log.error("@@@ [ttl] failure: ", e);
            return 0L;
        }
    }

    @Override
    public Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("@@@ [exists] failure: ", e);
            return false;
        }
    }

    @Override
    public void del(String... keys) {
        try {
            if (keys != null && keys.length > 0) {
                if (keys.length == 1) {
                    redisTemplate.delete(keys[0]);
                } else {
                    redisTemplate.delete(CollectionUtils.arrayToList(keys));
                }
            }
        } catch (Exception e) {
            log.error("@@@ [del] failure: ", e);
        }
    }

    @Override
    public <T> T get(String key) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            return o == null ? null : (T) o;
        } catch (Exception e) {
            log.error("@@@ [get] failure: ", e);
            return null;
        }
    }


    public Boolean set(String key, Object value) {
        return set(key, value, BaseMathConstants.ONE_DAY_4MILLISECONDS);
    }

    public Boolean set(String key, Object value, long time) {
        return set(key, value, time, TimeUnit.MILLISECONDS);
    }


    @Override
    public Boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        }  catch (Exception e) {
            log.error("@@@ [set] failure: key:{}", key, e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean setEx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("@@@ [setEx] failure: key:{}", key, e);
            return false;
        }
    }

    @Override
    public Boolean setNx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfPresent(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("@@@ [setEx] failure: key:{}", key, e);
            return false;
        }
    }

    @Override
    public Long incr(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("@@@ [incr] failure: key:{}", key, e);
            return 0L;
        }

    }

    @Override
    public <T> T hGet(String key, String hashKey) {
       try {
           Object o = redisTemplate.opsForHash().get(key, hashKey);
           return o == null ? null : (T) o;
       } catch (Exception e) {
           log.error("@@@ [hGet] failure: key:{}", key, e);
           return null;
       }
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error("@@@ [hSet] failure: key:{}", key, e);
            return false;
        }
    }


    public Boolean hSet(String key, String hashKey, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            expire(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error("@@@ [hSet] failure: key:{}", key, e);
            return false;
        }
    }


    @Override
    public <T> List<T> hmGet(String key, Collection<T> hashKeys) {
       try {
           return redisTemplate.opsForHash().multiGet(key, hashKeys);
       } catch (Exception e) {
           log.error("@@@ [hmGet] failure: key:{}", key, e);
           return new ArrayList<>();
       }
    }

    @Override
    public Boolean hmSet(String key, Map map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("@@@ [hmSet] failure: key:{}", key, e);
            return false;
        }
        return true;
    }

    @Override
    public <K, V> Map<K, V> hGetAll(String key) {
        try {
            Map map = redisTemplate.opsForHash().entries(key);
            return (Map<K, V>) map;
        } catch (Exception e) {
            log.error("@@@ [hGetAll] failure: key:{}", key, e);
            return null;
        }
   }


    @Override
    public Long hDel(String key, String... hashKey) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKey);
        } catch (Exception e) {
            log.error("@@@ [hDel] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public Boolean hExists(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("@@@ [hExists] failure: key:{}", key, e);
            return false;
        }

    }

    @Override
    public long hIncrBy(String key, String hashKey, long by) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, by);
        } catch (Exception e) {
            log.error("@@@ [hIncrBy] failure: key:{}", key, e);
            return 0;
        }

    }


    @Override
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("@@@ [sAdd] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public <T> Set<T> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("@@@ [sMembers] failure: key:{}", key, e);
            return null;
        }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("@@@ [sMembers] failure: key:{}", key, e);
            return false;
        }
    }

    @Override
    public Long sCard(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("@@@ [sCard] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public Long sRem(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("@@@ [sRem] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public Boolean zADD(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("@@@ [zADD] failure: key:{}", key, e);
            return false;
        }
    }

    @Override
    public Long zRem(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("@@@ [zRem] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            log.error("@@@ [zRank] failure: key:{}", key, e);
            return 0L;
        }
    }


    @Override
    public Long lPush(String key, Object... values) {
        try {
            if (values.length > 1) {
                return redisTemplate.opsForList().leftPushAll(key, values);
            } else {
                return redisTemplate.opsForList().leftPush(key, values);
            }
        } catch (Exception e) {
            log.error("@@@ [lPush] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public <T> T lPop(String key) {
        try {
            Object o = redisTemplate.opsForList().leftPop(key);
            return o == null ? null : (T) o;
        } catch (Exception e) {
            log.error("@@@ [lPop] failure: key:{}", key, e);
            return null;
        }
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end) {
        try {
            return (List<T>)redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("@@@ [lRange] failure: key:{}", key, e);
            return null;
        }
    }

    @Override
    public Long rPush(String key, Object... values) {
        try {
            if (values.length > 1) {
                return redisTemplate.opsForList().rightPushAll(key, values);
            } else {
                return redisTemplate.opsForList().rightPush(key, values);
            }
        } catch (Exception e) {
            log.error("@@@ [rPush] failure: key:{}", key, e);
            return 0L;
        }
    }

    @Override
    public <T> T rPop(String key) {
        try {
            Object o = redisTemplate.opsForList().rightPop(key);
            return o == null ? null : (T) o;
        } catch (Exception e) {
            log.error("@@@ [rPop] failure: key:{}", key, e);
            return null;
        }
    }




}

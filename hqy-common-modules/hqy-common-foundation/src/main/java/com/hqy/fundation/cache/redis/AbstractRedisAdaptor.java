package com.hqy.fundation.cache.redis;

import com.hqy.foundation.cache.redis.RedisService;
import com.hqy.fundation.cache.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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
            throw new RedisException("@@@ [flushDb] failure: ", e);
        }
    }

    @Override
    public void expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            throw new RedisException("@@@ [expire] failure: ", e);
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
            throw new RedisException("@@@ [ttl] failure: ", e);
        }
    }

    @Override
    public Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            throw new RedisException("@@@ [exists] failure: ", e);
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            throw new RedisException("@@@ [keys] failure, pattern: " + pattern, e);
        }
    }

    @Override
    public Set<String> scan(String matchKey) {
        try {
            return (Set<String>)redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> keys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(1000).build());
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
                return keys;
            });
        } catch (Exception e) {
            throw new RedisException("@@@ [scan] failure, matchKey: " + matchKey, e);
        }
    }

    @Override
    public Boolean del(String... keys) {
        try {
            if (keys != null && keys.length > 0) {
                if (keys.length == 1) {
                    return redisTemplate.delete(keys[0]);
                } else {
                    return redisTemplate.delete(CollectionUtils.arrayToList(keys)) != null;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RedisException("@@@ [del] failure: ", e);
        }
    }

    @Override
    public <T> T get(String key) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            return o == null ? null : (T) o;
        } catch (Exception e) {
            throw new RedisException("@@@ [get] failure: ", e);
        }
    }


    public Boolean set(String key, Object value) {
        return set(key, value, null);
    }

    public Boolean set(String key, Object value, Long time) {
        return set(key, value, time, TimeUnit.MILLISECONDS);
    }


    @Override
    public Boolean set(String key, Object value, Long time, TimeUnit timeUnit) {
        try {
            if (time == null || time == 0L) {
                redisTemplate.opsForValue().set(key, value);
            } else {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            }
        }  catch (Exception e) {
            throw new RedisException("@@@ [set] failure: key: " + key, e);
        }
        return true;
    }

    @Override
    public Boolean setEx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RedisException("@@@ [setEx] failure: key: " + key, e);
        }
    }

    @Override
    public Boolean setNx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfPresent(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RedisException("@@@ [setEx] failure: key: " + key, e);
        }
    }

    @Override
    public Long incr(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            throw new RedisException("@@@ [incr] failure: key: " + key, e);
        }

    }

    @Override
    public <T> T hGet(String key, String hashKey) {
       try {
           Object o = redisTemplate.opsForHash().get(key, hashKey);
           return o == null ? null : (T) o;
       } catch (Exception e) {
           throw new RedisException("@@@ [hGet] failure: key: " + key, e);
       }
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            throw new RedisException("@@@ [hSet] failure: key: " + key, e);
        }
    }


    public Boolean hSet(String key, String hashKey, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            expire(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            throw new RedisException("@@@ [hSet] failure: key: " + key, e);
        }
    }


    @Override
    public <T> List<T> hmGet(String key, Collection<String> hashKeys) {
        try {
            return redisTemplate.opsForHash().multiGet(key, hashKeys);
        } catch (Exception e) {
            throw new RedisException("@@@ [hmGet] failure: key: " + key, e);
        }
    }


    @Override
    public Boolean hmSet(String key, Map map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            throw new RedisException("@@@ [hmSet] failure: key: " + key, e);
        }
        return true;
    }

    @Override
    public <K, V> Map<K, V> hGetAll(String key) {
        try {
            Map map = redisTemplate.opsForHash().entries(key);
            return (Map<K, V>) map;
        } catch (Exception e) {
            throw new RedisException("@@@ [hGetAll] failure: key: " + key, e);
        }
   }


    @Override
    public Long hDel(String key, String... hashKey) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKey);
        } catch (Exception e) {
            throw new RedisException("@@@ [hDel] failure: key:{}" + key, e);
        }
    }

    @Override
    public Boolean hExists(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            throw new RedisException("@@@ [hExists] failure: key: " + key, e);
        }

    }

    @Override
    public long hIncrBy(String key, String hashKey, long by) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, by);
        } catch (Exception e) {
            throw new RedisException("@@@ [hIncrBy] failure: key: " + key, e);
        }

    }


    @Override
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            throw new RedisException("@@@ [sAdd] failure: key: " + key, e);
        }
    }

    public Long sAdd(String key, long time, TimeUnit timeUnit, Object values){
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            expire(key, time, timeUnit);
            return count;
        } catch (Exception e) {
            throw new RedisException("@@@ [sAdd] failure, key: " + key);
        }
    }


    @Override
    public <T> Set<T> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            throw new RedisException("@@@ [sMembers] failure: key: " + key, e);
        }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            throw new RedisException("@@@ [sMembers] failure: key: " + key, e);
        }
    }

    @Override
    public Long sCard(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            throw new RedisException("@@@ [sCard] failure: key: " + key, e);
        }
    }

    @Override
    public Long sRem(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            throw new RedisException("@@@ [sRem] failure: key: " + key, e);
        }
    }

    @Override
    public Boolean zADD(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            throw new RedisException("@@@ [zADD] failure: key: " + key, e);
        }
    }

    @Override
    public Long zRem(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            throw new RedisException("@@@ [zRem] failure: key: " + key, e);
        }
    }

    @Override
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            throw new RedisException("@@@ [zRank] failure: key: " + key, e);
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
            throw new RedisException("@@@ [lPush] failure: key: " + key, e);
        }
    }

    @Override
    public <T> T lPop(String key) {
        try {
            Object o = redisTemplate.opsForList().leftPop(key);
            return o == null ? null : (T) o;
        } catch (Exception e) {
            throw new RedisException("@@@ [lPop] failure: key: " + key, e);
        }
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end) {
        try {
            return (List<T>)redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            throw new RedisException("@@@ [lRange] failure: key: " + key, e);
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
            throw new RedisException("@@@ [rPush] failure: key: " + key, e);
        }
    }

    @Override
    public <T> T rPop(String key) {
        try {
            Object o = redisTemplate.opsForList().rightPop(key);
            return o == null ? null : (T) o;
        } catch (Exception e) {
            throw new RedisException("@@@ [rPop] failure: key: " + key, e);
        }
    }




}

package com.hqy.cloud.redis.server;

import com.hqy.cloud.common.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Slf4j
public abstract class AbstractTemplateOperations implements RedisTemplateOperations {
    private final RedisTemplate<String, Object> redisTemplate;

    protected AbstractTemplateOperations(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public final void flushDb() {
        try {
            redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
                redisConnection.flushDb();
                return "ok";
            });
        } catch (Exception e) {
            log.error("Failed execute to redis [flushDb].", e);
        }
    }

    @Override
    public final void expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            log.error("Failed execute to redis [expire]. RedisKey: {}.", key, e);
        }
    }

    public final Long ttl(String key) {
        return ttl(key, TimeUnit.SECONDS);
    }

    @Override
    public final Long ttl(String key, TimeUnit timeUnit) {
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (Exception e) {
            log.error("Failed execute to redis [ttl]. RedisKey: {}.", key, e);
            return 0L;
        }
    }

    @Override
    public final Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Failed execute to redis [exists]. RedisKey: {}.", key, e);
            return false;
        }
    }

    @Override
    public final Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Failed execute to redis [keys]. Pattern: {}.", pattern, e);
            return null;
        }
    }

    @Override
    public final Set<String> scan(String matchKey) {
        return scan(matchKey, 1000);
    }

    public final Set<String> scan(String matchKey, int matchCount) {
        try {
            return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> keys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("*" + matchKey + "*").count(matchCount).build());
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
                return keys;
            });
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [scan]. MatchKey: " + matchKey,  e);
        }
    }


    @Override
    public final Boolean del(String... keys) {
        try {
            if (keys != null && keys.length > 0) {
                return redisTemplate.delete(Arrays.asList(keys)) != null;
            }
        } catch (Exception e) {
            log.error("Failed execute to redis [del]. RedisKey: {}.", keys, e);
        }
        return false;
    }

    @Override
    public final Long incr(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [incr]. Redis key: " + key, e);
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
        boolean setEx = time != null && time > 0L;
        try {
            if (setEx) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                redisTemplate.opsForValue().set(key, value);
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
            return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
        } catch (Exception e) {
            log.error("Failed execute to redis [setEx]. RedisKey: {}.", key, e);
            return false;
        }
    }

    @Override
    public Boolean setNx(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfPresent(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed execute to redis [setNx]. RedisKey: {}.", key, e);
            return false;
        }
    }


    @Override
    public Long hDel(String key, Object... hashKey) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKey);
        } catch (Exception e) {
            log.error("Failed execute to redis [hDel]. RedisKey: {}.", key, e);
            return 0L;
        }
    }

    @Override
    public Boolean hExists(String key, Object hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("Failed execute to redis [hExists]. RedisKey: {}.", key, e);
            return false;
        }
    }

    @Override
    public long hIncrBy(String key, Object hashKey, long by) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, by);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [hExists].  RedisKey: " + key, e);
        }
    }

    @Override
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Failed execute to redis [sAdd]. RedisKey: {}.", key, e);
            return 0L;
        }
    }

    @Override
    public Long sAdd(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            expire(key, time, timeUnit);
            return count;
        } catch (Exception e) {
            log.error("Failed execute to redis [sAdd]. RedisKey: {}.", key, e);
            return 0L;
        }
    }


    @Override
    public Long sCard(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [sCard]. key: " + key, e);
        }
    }


    @Override
    public Long sRem(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [sRem]. key: " + key, e);
        }
    }


    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [sIsMember]. key: " + key, e);
        }
    }

    @Override
    public Boolean zADD(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [zADD]. key: " + key, e);
        }
    }

    @Override
    public Long zRem(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [zRem].  key: " + key, e);
        }
    }

    @Override
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [zRank]. key: " + key, e);
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
            throw new RedisException("Failed execute to redis [lPush]. key: " + key, e);
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
            throw new RedisException("Failed execute to redis [rPush]. key: " + key, e);
        }
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        try {
            Boolean setBit = redisTemplate.opsForValue().setBit(key, offset, value);
            setBit = setBit != null && setBit;
            return setBit;
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [setBit]. Key: " + key, e);
        }
    }

    @Override
    public boolean getBit(String key, long offset) {
        try {
            Boolean getBit = redisTemplate.opsForValue().getBit(key, offset);
            getBit = getBit != null && getBit;
            return getBit;
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [getBit]. Key: " + key, e);
        }
    }

    @Override
    public long bitCount(String key) {
        try {
            Long execute = redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));
            execute = execute == null ? 0 : execute;
            return execute;
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [bitCount]. Key: " + key, e);
        }
    }

    @Override
    public List<Long> bigField(String key, int limit, int offset) {
        try {
            return redisTemplate.opsForValue().
                    bitField(key, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(limit)).valueAt(offset));
        } catch (Exception e) {
            throw new RedisException("Failed execute to redis [bigField]. Key: " + key, e);
        }
    }

}

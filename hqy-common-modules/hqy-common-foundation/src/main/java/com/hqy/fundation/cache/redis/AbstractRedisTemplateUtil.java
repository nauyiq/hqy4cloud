package com.hqy.fundation.cache.redis;

import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

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
public abstract class AbstractRedisTemplateUtil {

    protected AbstractRedisTemplateUtil(RedisTemplate<String, Object>  redisTemplate) {
        AbstractRedisTemplateUtil.redisTemplate = redisTemplate;
    }

    /**
     * 由子类进行构造，由子类选择构造的redis客户端，是lettuce还是jedis
     */
    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * StringRedisTemplate
     */
    private static final StringRedisTemplate stringRedisTemplate =
            SpringContextHolder.getBean(StringRedisTemplate.class);

    /**
     * 默认缓存时间 单位秒 设置成1天
     * */
    private static final long DEFAULT_CACHE_SECONDS = 60 * 60 * 24;



    /**
     * 删除RedisTemplate中配置的db的所有key
     */
    public void flushDB() {
        try {
            redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
                redisConnection.flushDb();
                return "ok";
            });
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 指定缓存失效时间
     * @param key redis key
     * @param time 秒
     */
    public void expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0 代表为永久有效
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }

    }


    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        try {
            if (key != null && key.length > 0) {
                if (key.length == 1) {
                    redisTemplate.delete(key[0]);
                } else {
                    redisTemplate.delete(CollectionUtils.arrayToList(key));
                }
            }
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 返回前缀匹配到的key的数据集合
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        try {
            return stringRedisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }

    // ==================================================== String ==============================================


    /**
     * 序列化Object类型的get
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }

    }

    /**
     * String类型的get
     * @param key
     * @return
     */
    public String getString(String key) {
        try {
            return key == null ? null : stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }

    /**
     * 序列化Object类型的set
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            set(key, value, DEFAULT_CACHE_SECONDS);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public Boolean set(String key, Object value, Long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }


    /**
     * String类型的set 使用默认缓存时间
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        try {
            set(key, value, DEFAULT_CACHE_SECONDS);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 缓存String类型的value， 使用指定时间， 单位秒
     * @param key
     * @param value
     * @param second
     */
    public void set(String key, String value, Long second) {
        try {
            if (second > 0) {
                stringRedisTemplate.opsForValue().set(key, value, second, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }


    /**
     * 如果不存在则设置
     * @param key
     * @param value
     * @param second
     * @return
     */
    public Boolean setEx(String key, Object value, Long second) {
        try {
            return redisTemplate.opsForValue().setIfPresent(key, value, second, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }


    /**
     * 如果不存在则设置
     * 设置成功返回 true
     * @param key
     * @param value
     * @param second
     * @return
     */
    public Boolean setEx(String key, String value, Long second) {
        try {
            return Boolean.FALSE.equals(stringRedisTemplate.opsForValue().setIfPresent(key, value, second, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }



    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public Long incr(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }

    }




    /**
     * hget
     * @param key  键 不能为null
     * @param field 项 不能为null
     * @return 值
     */
    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }

    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public void hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * HashSet 并设置时间
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public void hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public void hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public void hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 删除hash表中的值
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        try {
            redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        try {
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }

    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hIncr(String key, String item, double by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, by);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0;
        }

    }

    /**
     * hash递减
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hDecr(String key, String item, double by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, -by);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0;
        }
    }




    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }


    public Set<String> strSMembers(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0L;
        }
    }

    public void strSAdd(String key, String... values) {
        try {
            stringRedisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }


    public void strSAdd(String key, long time, String... values) {
        try {
            stringRedisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }

    /**
     * 将set数据放入缓存
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sAdd(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0L;
        }
    }



    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public Long scard(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0L;
        }
    }

    /**
     * 移除值为value的
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long sMove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0L;
        }
    }

    public void sMove(String key, String... values) {
        try {
            stringRedisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("操作失败: ", e);
        }
    }




    /**
     * 添加到有序set的一个或多个成员，或更新分数如果它已经存在
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return
     */
    public Boolean zaad(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }



    /**
     * 获取list缓存的内容
     * @param key   键
     * @param start 开始 0 是第一个元素
     * @param end   结束 -1代表所有值
     * @return
     * @取出来的元素 总数 end-start+1
     */
    public List<Object> lrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public Long llen(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0L;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lindex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean rpush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value, DEFAULT_CACHE_SECONDS);
            return true;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean rpush(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean rpush(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean rpush(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lset(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lrem(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return 0L;
        }
    }


}

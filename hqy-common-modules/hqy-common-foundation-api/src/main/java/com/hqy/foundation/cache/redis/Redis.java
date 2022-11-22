package com.hqy.foundation.cache.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis Operations.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 15:23
 */
public interface Redis extends RedisTemplateCommonOperations {

    /**
     * 根据key获取对应的value
     * @param key redis key
     * @param <T> return type
     * @return value
     */
    <T> T get(String key);


    /**
     * 往hash表中存放一条数据
     * @param key       redis key
     * @param hashKey   hashKey
     * @param value     value
     * @return 操作是否成功
     */
    void hSet(String key, Object hashKey, Object value);


    /**
     * 往hash表中存放一条数据
     * @param key       redis key.
     * @param hashKey   hash field
     * @param value     value
     * @param time      过期时间
     * @param timeUnit  时间单位
     */
    void hSet(String key, Object hashKey, Object value, long time, TimeUnit timeUnit);


    /**
     * 设置多个hash值
     * @param key   redis key
     * @param data  data
     */
    void hmSet(String key, Map<Object, Object> data);

    /**
     * 获取hash结构的value
     * @param key       redis key
     * @param hashKey   hashKey
     * @param <T>       return type
     * @return value
     */
    <T> T hGet(String key, Object hashKey);


    /**
     * 获取hash结构的多个value
     * @param key       redis key
     * @param hashKeys  hashKeys
     * @param <T>       return type
     * @return value
     */
    <T> List<T> hmGet(String key, List<Object> hashKeys);


    /**
     * 获取hash表中的所有key,value
     * @param key redis key
     * @param <K> ket type
     * @param <V> value type
     * @return map
     */
    <K, V> Map<K, V> hGetAll(String key);


    /**
     * 获取集合中的所有元素
     * @param key redis key
     * @param <T> type
     * @return 集合
     */
    <T> Set<T> sMembers(String key);


    /**
     * 从队列的左边出队一个元素
     * @param key redis key
     * @param <T> return type
     * @return value
     */
    <T> T lPop(String key);

    /**
     * 从队列的左边获取制定返回的元素
     * @param key   redis key
     * @param start 开始的偏移量
     * @param end   结束的偏移量
     * @param <T>   return type
     * @return 元素集合
     */
    <T> List<T> lRange(String key, long start, long end);

    /**
     * 从队列的右边出队一个元素
     * @param key redis key
     * @param <T> return type
     * @return value
     */
    <T> T rPop(String key);

}

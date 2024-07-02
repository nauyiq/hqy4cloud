package com.hqy.cloud.cache.redis.server;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis String类型操作service，基于StringRedisTemplate
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public interface RedisStringTemplateOperations extends RedisTemplateOperations {

    /**
     * 根据key获取对应的value
     * @param key   key
     * @return      bean.
     */
    String get(String key);

    /**
     * 根据key获取对应的value
     * @param key   key
     * @param clazz type.
     * @return      bean.
     */
    <T> T get(String key, Class<T> clazz);


    /**
     * 往hash表中存放一条数据
     * @param key       redis key
     * @param hashKey   hashKey
     * @param value     value
     * @return 操作是否成功
     */
    void hSet(String key, String hashKey, Object value);

    /**
     *  往hash表中存放一条数据
     * @param key       redis key
     * @param hashKey   hashKey
     * @param value     value
     * @param time      time
     * @param timeUnit  时间单位
     */
    void hSet(String key, String hashKey, Object value, long time, TimeUnit timeUnit);


    /**
     * 设置多个hash值
     * @param key   redis key
     * @param data  data
     */
    void hmSet(String key, Map<String, String> data);

    /**
     * 获取hash结构的value
     * @param key         redis key
     * @param hashField   hash Field
     * @return            value
     */
    String hGet(String key, String hashField);

    /**
     * 获取hash结构的value
     * @param key       redis key
     * @param hashField hash Field
     * @param clazz     type
     * @return          value
     */
    <T> T hGet(String key, String hashField, Class<T> clazz);

    /**
     * 获取hash结构的多个value
     * @param key       redis key
     * @param hashKeys  hashKeys
     * @return value
     */
    List<String> hmGet(String key, Collection<Object> hashKeys);

    /**
     * 获取hash结构的values
     * @param key         redis key
     * @param hashKeys    hash Fields
     * @param clazz       type
     * @return            values
     */
    <T> List<T> hmGet(String key,  Collection<Object> hashKeys, Class<T> clazz);


    /**
     * 获取hash表中的所有key,value
     * @param key redis key
     * @return    map
     */
    Map<String, String> hGetAll(String key);

    /**
     * 获取hash表中的所有key,value
     * @param key   redis key
     * @param clazz type
     * @return      map
     */
    <T> Map<String, T> hGetAll(String key, Class<T> clazz);


    /**
     * 获取集合中的所有元素
     * @param key redis key
     * @return    集合
     */
    Set<String> sMembers(String key);

    /**
     * 获取集合中的所有元素
     * @param key   redis key
     * @param clazz type
     * @return      集合
     */
    <T> Set<T> sMembers(String key, Class<T> clazz);

    /**
     * 从队列的左边出队一个元素
     * @param key redis key
     * @return    value
     */
    String lPop(String key);

    /**
     * 从队列的左边出队一个元素
     * @param key   redis key
     * @param clazz type
     * @return      value
     */
    <T> T lPop(String key, Class<T> clazz);

    /**
     * 从队列的左边获取制定返回的元素
     * @param key   redis key
     * @param start 开始的偏移量
     * @param end   结束的偏移量
     * @return 元素集合
     */
    List<String> lRange(String key, long start, long end);


    /**
     * 从队列的左边获取制定返回的元素
     * @param key   redis key
     * @param start 开始的偏移量
     * @param end   结束的偏移量
     * @param clazz type
     * @return      元素集合
     */
    <T> List<T> lRange(String key, long start, long end, Class<T> clazz);


    /**
     * 从队列的右边出队一个元素
     * @param key redis key
     * @return    value
     */
    String rPop(String key);

    /**
     * 从队列的右边出队一个元素
     * @param key   redis key
     * @param clazz type
     * @return      value
     */
    <T> T rPop(String key, Class<T> clazz);

    /**
     * 获取redis template
     * @return {@link StringRedisTemplate}
     */
    StringRedisTemplate getRedisTemplate();

}

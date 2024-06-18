package com.hqy.cloud.cache.redis.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用的redis操作.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public interface RedisTemplateOperations {


    /**
     * 删除RedisTemplate中配置的db的所有key
     */
    void flushDb();

    /**
     * 指点某个key的过期时间
     * @param key        redis key
     * @param time       过期时间
     * @param timeUnit   单位
     */
    void expire(String key, long time, TimeUnit timeUnit);


    /**
     * 判断某个key还有多久过期 -1为永久
     * @param key       redis key
     * @param timeUnit  单位
     * @return 过期时间
     */
    Long ttl(String key, TimeUnit timeUnit);

    /**
     * 判断某个key存不存在
     * @param key redis key
     * @return 是否存在
     */
    Boolean exists(String key);

    /**
     * 模糊查询返回匹配到键
     * 注意！！！ keys的操作会导致数据库暂时被锁住，其他的请求都会被堵塞；业务量大的时候会出问题 使用需谨慎
     * 如果遇到必须使用模糊的业务场景 推荐使用redis的scan命令 基于游标进行匹配。
     * @param pattern 正则
     * @return keys
     */
    Set<String> keys(String pattern);

    /**
     * 增量的迭代key 数据结构为String
     * @param matchKey 需要匹配的key
     * @return keys
     */
    Set<String> scan(String matchKey);

    /**
     * 根据key删除相应value. 可批量删除.
     * @param keys redis key
     * @return 是否删除成功
     */
    Boolean del(String... keys);


    /**
     * 保存一个key,value到redis中 数据结果为String
     * @param key       redis key
     * @param value     值
     * @param time      时间
     * @param timeUnit  单位
     * @return 操作是否成功
     */
    Boolean set(String key, Object value, Long time, TimeUnit timeUnit);

    /**
     * 保存一个key,value 并设置过期时间 这是一个原子操作
     * @param key        redis key
     * @param value      值
     * @param time       时间
     * @param timeUnit   单位
     * @return 操作是否成功
     */
    Boolean setEx(String key, Object value, long time, TimeUnit timeUnit);


    /**
     * 只有key不存在的时候才会返回true 这是一个原子操作
     * @param key        redis key
     * @param value      值
     * @param time       时间
     * @param timeUnit   单位
     * @return 操作是否成功
     */
    Boolean setNx(String key, Object value, long time, TimeUnit timeUnit);

    /**
     * 递增
     * @param key    redis key
     * @param delta  要增加几(大于0)
     * @return 递增后的大小
     */
    Long incr(String key, long delta);



    /**
     * 根据hash表的key   删除相应的值
     * @param key       redis key
     * @param hashKeys  hashKeys
     * @return          影响的行数
     */
    Long hDel(String key, Object... hashKeys);


    /**
     * 判断hashKey是否存在于hash中
     * @param key       redis key
     * @param hashKey   hashKey
     * @return Boolean
     */
    Boolean hExists(String key, Object hashKey);

    /**
     * 将hash中指定域的值增加给定的数字
     * @param key       redis key
     * @param hashKey   hashKey
     * @param by        增加的数字大小
     * @return 增加后的数字
     */
    long hIncrBy(String key, Object hashKey, long by);

    /**
     * 添加一个或多个元素到集合set中
     * @param key       redis key
     * @param values    元素
     * @return 影响的行数
     */
    Long sAdd(String key, Object... values);

    /**
     * 添加一个或多个元素到集合set中
     * @param key       redis key
     * @param time      时间
     * @param timeUnit  时间单位
     * @param values    values
     * @return          影响的行数
     */
    Long sAdd(String key, long time, TimeUnit timeUnit, Object... values);


    /**
     * 获取集合里面元素的数量
     * @param key redis key
     * @return   元素的数量
     */
    Long sCard(String key);


    /**
     * 从集合中删除一个或多个元素
     * @param key     redis key
     * @param values  delete value
     * @return        影响的行数
     */
    Long sRem(String key, Object... values);

    /**
     * 判断给定的一个值是否是set的成员
     * @param key   redis key
     * @param value check value
     * @return      是否是set的成员
     */
    Boolean sIsMember(String key, Object value);


    /**
     * 添加到有序set的集合中, 如果存在则更新分数
     * @param key   redis key
     * @param value 值
     * @param score 分数
     * @return      是否成功
     */
    Boolean zADD(String key, Object value, double score);


    /**
     * 从有序集合中删除一个或多个元素
     * @param key     redis key
     * @param values  值
     * @return 影响的行数
     */
    Long zRem(String key, Object... values);


    /**
     * 确定某个值在集合中的排名 即索引位置
     * @param key   redis key
     * @param value 值
     * @return 索引
     */
    Long zRank(String key, Object value);

    /**
     * 从队列的左边入队一个或多个元素
     * @param key    redis key
     * @param values 元素
     * @return 影响的行数
     */
    Long lPush(String key, Object... values);



    /**
     * 从队列的右边入队一个或多个元素
     * @param key    redis key
     * @param values 元素
     * @return       影响的行数
     */
    Long rPush(String key, Object... values);


    /**
     * 设置一个位图
     * @param key    redis key
     * @param offset 偏移量
     * @param value  value
     * @return       result.
     */
    boolean setBit(String key, long offset, boolean value);

    /**
     * 获取位图数据
     * @param key    redis key
     * @param offset 偏移量
     * @return       value.
     */
    boolean getBit(String key, long offset);

    /**
     * 获取位图的数目
     * @param key  redis key
     * @return     位图元素个数
     */
    long bitCount(String key);

    /**
     * 获取指定范围内位图的元素值
     * @param key     redis key
     * @param limit   查多少个元素
     * @param offset  从几个元素开始查
     * @return        范围内位图的元素值
     */
    List<Long> bigField(String key, int limit, int offset);


}

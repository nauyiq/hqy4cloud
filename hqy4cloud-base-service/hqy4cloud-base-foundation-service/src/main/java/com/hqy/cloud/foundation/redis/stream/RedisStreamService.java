package com.hqy.cloud.foundation.redis.stream;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.foundation.redis.RedisConstants;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;

/**
 * 针对redis stream数据结构的service <br/>
 * Stream底层采用了类似于日志的数据结构，每个Stream都是由一个或多个日志实现的。每个消息包含一个唯一的ID和附加的其他数据。。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/17
 */
@SuppressWarnings("unchecked")
public interface RedisStreamService {

    /**
     * 添加一个消息到stream中, 追加的方式，
     * @param redisKey     redis key
     * @param messageValue 消息值
     * @return             消息id
     */
    default String add(String redisKey, Object messageValue) {
        return this.add(redisKey, StringConstants.EMPTY, messageValue);
    }

    /**
     * 添加一个消息到stream中, 追加的方式，
     * @param redisKey      redis key
     * @param messageId     消息id
     * @param messageValue  消息值
     * @return              消息id
     */
    String add(String redisKey, String messageId, Object messageValue);

    /**
     * 裁剪流中的消息
     * @param redisKey            redis key
     * @param count               流的长度
     * @return                    裁剪掉的元素个数
     */
    default Long trim(String redisKey, long count) {
        return this.trim(redisKey, count, false);
    }

    /**
     * 裁剪流中的消息
     * @param redisKey            redis key
     * @param count               流的长度
     * @param approximateTrimming 是否采用近似值进行裁剪，增加效率
     * @return                    裁剪掉的元素个数
     */
    Long trim(String redisKey, long count, boolean approximateTrimming);

    /**
     * 删除流中的消息
     * @param redisKey   redis key
     * @param messageIds 消息id集合
     * @return           删除的消息个数.
     */
    Long del(String redisKey, Collection<String> messageIds);

    /**
     * 返回流的长度
     * @param redisKey redis key
     * @return         流的长度
     */
    Long len(String redisKey);

    /**
     * 范围查找消息
     * @param redisKey redis key
     * @param range    访问查找，range {@link Range}
     * @return         消息记录 {@link MapRecord}
     */
    List<MapRecord<String, String, Object>> range(String redisKey, Range<String> range);

    /**
     * 创建消费者组，默认创建从流头部开始消费的消费者组，即该消费者组只接收新的消息
     * @param redisKey   redis key
     * @param group      消费者组
     */
    default void createConsumerGroup(String redisKey, String group) {
        this.createConsumerGroup(redisKey, group, ReadOffset.latest());
    }

    /**
     * 创建消费者组, 并且指定该消费者组的读偏移量
     * @param redisKey   redis key
     * @param group      消费者组
     * @param readOffset 读消息offset
     */
    void createConsumerGroup(String redisKey, String group, ReadOffset readOffset);

    /**
     * 读消息
     * @param classType     消息类型
     * @param options       读操作
     * @param streamOffsets 读偏移量
     * @return              {@link ObjectRecord}
     */
    <T> List<ObjectRecord<String, T>> readWithRecord(Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets);


    /**
     * 读消息
     * @param classType     消息类型
     * @param options       读操作
     * @param streamOffsets 读偏移量
     * @return              读取的消息
     */
    <T> List<T> read(Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets);

    /**
     * 读取消息
     * @param group         消费者组
     * @param name          消费者名称
     * @param classType     消息类型
     * @param options       读消息操作
     * @param streamOffsets 消息offset
     * @return              读取到的消息
     */
    @SuppressWarnings("unchecked")
    <T> List<T> readGroup(String group, String name, Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets);


    /**
     * 读取消息
     * @param group         消费者组
     * @param name          消费者名称
     * @param classType     消息类型
     * @param options       读消息操作
     * @param streamOffsets 消息offset
     * @return              读取到的消息
     */
    @SuppressWarnings("unchecked")
    <T> List<ObjectRecord<String, T>> readGroupWithRecord(String group, String name, Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets);

    /**
     * 查看消费者下有多少条pending的消息
     * @param redisKey redis key
     * @param group    消费者组
     * @param name     消费者名
     * @return         pending的消息id
     */
    default List<String> pending(String redisKey, String group, String name) {
        return pending(redisKey, group, name, Range.unbounded(), RedisConstants.STREAM_DEFAULT_PENDING_LIMIT_COUNT);
    }

    /**
     * 查看消费者下有多少条pending的消息
     * @param redisKey redis key
     * @param group    消费者组
     * @param name     消费者名
     * @param range    范围
     * @param count    查看的条数
     * @return         pending的消息id
     */
    List<String> pending(String redisKey, String group, String name, Range<String> range, long count);

    /**
     * 标记消息为已处理
     * @param redisKey redis key
     * @param group    消费者组
     * @param ids      消息id列表
     * @return         确认处理的消息长度
     */
    default Long ack(String redisKey, String group, List<String> ids) {
        return ack(redisKey, group, ids.toArray(ids.toArray(new String[0])));
    }

    /**
     * 标记消息为已处理
     * @param redisKey redis key
     * @param group    消费者组
     * @param ids      消息id列表
     * @return         确认处理的消息长度
     */
    Long ack(String redisKey, String group, String... ids);


    /**
     * 查找消费者组中的消费者
     * @param redisKey redis key
     * @param group    消费者组
     * @return         组成员
     */
    StreamInfo.XInfoConsumers queryConsumers(String redisKey, String group);


    RedisTemplate<String, Object> getTemplate();

}

package com.hqy.cloud.redis.stream.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.redis.stream.RedisStreamService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class RedisStreamServiceImpl implements RedisStreamService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String add(String redisKey, String messageId, Object messageValue) {
        AssertUtil.isFalse(StringUtils.isBlank(redisKey) || messageValue == null,
                "Failed execute to add stream message, the param is null.");

        Map<String, Object> streamValueMap = JsonUtil.toMap(messageValue);
        RecordId recordId;
        if (StringUtils.isNotBlank(messageId)) {
            messageId = System.currentTimeMillis() + StrUtil.DASHED + messageId;
            recordId = RecordId.of(messageId);
            MapRecord<String, String, Object> record = StreamRecords.newRecord()
                    .withId(recordId)
                    .ofMap(streamValueMap)
                    .withStreamKey(redisKey);
            this.redisTemplate.opsForStream().add(record);
        } else {
            recordId = this.redisTemplate.opsForStream().add(redisKey, streamValueMap);
        }
        if (recordId == null) {
            log.warn("The recordId is null by add stream, redisKey: {}.", redisKey);
            return StringConstants.EMPTY;
        }
        return recordId.getValue();
    }


    @Override
    public Long trim(String redisKey, long count, boolean approximateTrimming) {
        AssertUtil.notEmpty(redisKey, "Redis key should not be empty.");

        if (count < 0) {
            count = 0;
        }
        return approximateTrimming ? this.redisTemplate.opsForStream().trim(redisKey, count, true) :
                this.redisTemplate.opsForStream().trim(redisKey, count);
    }

    @Override
    public Long del(String redisKey, Collection<String> messageIds) {
        AssertUtil.isFalse(StringUtils.isBlank(redisKey) || CollectionUtils.isEmpty(messageIds),
                "Failed execute to del stream message, the param is null.");

        RecordId[] recordIds = messageIds.stream().map(RecordId::of).toArray(RecordId[]::new);
        return this.redisTemplate.opsForStream().delete(redisKey, recordIds);
    }

    @Override
    public Long len(String redisKey) {
        AssertUtil.notEmpty(redisKey, "Redis key should not be empty.");
        return this.redisTemplate.opsForStream().size(redisKey);
    }

    @Override
    public List<MapRecord<String, String, Object>> range(String redisKey, Range<String> range) {
        AssertUtil.notEmpty(redisKey, "Redis key should not be empty.");
        AssertUtil.notNull(range, "Stream range should not be null.");
        StreamOperations<String, String, Object> stream = this.redisTemplate.opsForStream();
        return stream.range(redisKey, range);
    }

    @Override
    public void createConsumerGroup(String redisKey, String group, ReadOffset readOffset) {
        AssertUtil.isFalse(StringUtils.isBlank(redisKey) || StringUtils.isBlank(group), "Failed execute to create consumer group, the params is empty.");

        StreamOperations<String, String, Object> stream = this.redisTemplate.opsForStream();
        stream.createGroup(redisKey, readOffset, group);
    }

    @Override
    public <T> List<ObjectRecord<String, T>> readWithRecord(Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets) {
        AssertUtil.isFalse( classType == null || options == null || streamOffsets == null, "Failed execute to read message, the params is empty.");

        StreamOperations<String, String, Object> stream = this.redisTemplate.opsForStream();
        return stream.read(classType, options, streamOffsets);
    }

    @Override
    public <T> List<T> read(Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets) {
        List<ObjectRecord<String, T>> objectRecords = readWithRecord(classType, options, streamOffsets);
        return CollectionUtils.isNotEmpty(objectRecords) ? objectRecords.stream().map(ObjectRecord::getValue).toList() : Collections.emptyList();
    }


    @Override
    public <T> List<T> readGroup(String group, String name, Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets) {
        AssertUtil.isFalse(StringUtils.isAnyBlank(group, name) || options == null || streamOffsets == null, "Failed execute to read message, the params is empty.");

        StreamOperations<String, String, Object> stream = this.redisTemplate.opsForStream();
        Consumer consumer = Consumer.from(group, name);
        List<ObjectRecord<String, T>> records = stream.read(classType, consumer, options, streamOffsets);
        return CollectionUtils.isNotEmpty(records) ? records.stream().map(ObjectRecord::getValue).toList() : Collections.emptyList();
    }


    @Override
    public <T> List<ObjectRecord<String, T>> readGroupWithRecord(String group, String name, Class<T> classType, StreamReadOptions options, StreamOffset<String>... streamOffsets) {
        AssertUtil.isFalse(StringUtils.isAnyBlank(group, name) || options == null || streamOffsets == null, "Failed execute to read message, the params is empty.");

        StreamOperations<String, String, Object> stream = this.redisTemplate.opsForStream();
        Consumer consumer = Consumer.from(group, name);
        return stream.read(classType, consumer, options, streamOffsets);
    }

    @Override
    public List<String> pending(String redisKey, String group, String name, Range<String> range, long count) {
        AssertUtil.isFalse(StringUtils.isAnyBlank(redisKey, group, name), "Failed execute to stream pending.");

        StreamOperations<String, String, Object> streamOperations = this.redisTemplate.opsForStream();

        Consumer consumer = Consumer.from(group, name);
        PendingMessages pending = streamOperations.pending(redisKey, consumer,range, count);
        return pending.get().map(PendingMessage::getId).map(RecordId::getValue).toList();
    }

    @Override
    public Long ack(String redisKey, String group, String... ids) {
        AssertUtil.isFalse(StringUtils.isAnyBlank(redisKey, group) || ids.length == 0, "Failed execute to stream ack.");

        StreamOperations<String, String, Object> streamOperations = this.redisTemplate.opsForStream();
        return streamOperations.acknowledge(redisKey, group, ids);
    }

    @Override
    public StreamInfo.XInfoConsumers queryConsumers(String redisKey, String group) {
        AssertUtil.isFalse(StringUtils.isAnyBlank(redisKey, group), "Failed execute to query consumers.");

        StreamOperations<String, String, Object> streamOperations = this.redisTemplate.opsForStream();
        return streamOperations.consumers(redisKey, group);
    }

    @Override
    public RedisTemplate<String, Object> getTemplate() {
        return this.redisTemplate;
    }
}

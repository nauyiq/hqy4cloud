package com.hqy.cloud.foundation.cache.component;

import com.hqy.cloud.foundation.redis.stream.RedisStreamService;
import com.hqy.foundation.stream.Message;
import com.hqy.foundation.stream.StreamMessageCommonOperations;
import com.hqy.foundation.stream.StreamMessageId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/19
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class RedisStreamOperations implements StreamMessageCommonOperations {
    private final RedisStreamService redisStreamService;

    @Override
    public <P extends Comparable<P>, T> List<Message<T>> rangeWithClosed(String redisKey, boolean reverse, StreamMessageId<P> left, StreamMessageId<P> right) {
        Range<String> closed = (Range<String>) Range.closed(left.getId(), right.getId());
        List<MapRecord<String, String, Object>> range = redisStreamService.range(redisKey, closed);
        return StreamMessageCommonOperations.super.rangeWithClosed(redisKey, reverse, left, right);
    }

    @Override
    public <P extends Comparable<P>, T> List<Message<T>> rangeOWithOpen(String redisKey, boolean reverse, StreamMessageId<P> left, StreamMessageId<P> right) {
        return StreamMessageCommonOperations.super.rangeOWithOpen(redisKey, reverse, left, right);
    }

    @Override
    public <P extends Comparable<P>, T> List<Message<T>> rangeWithLeftOpenRightClosed(String redisKey, boolean reverse, StreamMessageId<P> left, StreamMessageId<P> right) {
        return StreamMessageCommonOperations.super.rangeWithLeftOpenRightClosed(redisKey, reverse, left, right);
    }

    @Override
    public <P extends Comparable<P>, T> List<Message<T>> rangeWithLeftClosedRightOpen(String redisKey, boolean reverse, StreamMessageId<P> left, StreamMessageId<P> right) {
        return StreamMessageCommonOperations.super.rangeWithLeftClosedRightOpen(redisKey, reverse, left, right);
    }
}

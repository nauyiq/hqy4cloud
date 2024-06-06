package com.hqy.cloud.foundation.redis.stream;

import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamMessage;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/6
 */
public abstract class RedisStreamMessage<R> implements StreamMessage<String, R> {
    private final R data;
    private final MessageId<String> id;

    public RedisStreamMessage(R data) {
        this(null, data);
    }

    public RedisStreamMessage(MessageId<String> id, R data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public MessageId<String> getId() {
        return id;
    }

    @Override
    public R gerValue() {
        return data;
    }

}

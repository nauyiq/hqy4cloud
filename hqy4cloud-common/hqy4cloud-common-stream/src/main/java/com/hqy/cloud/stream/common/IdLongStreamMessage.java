package com.hqy.cloud.stream.common;

import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamMessage;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/22
 */
public abstract class IdLongStreamMessage<R> implements StreamMessage<Long, R> {

    private final R data;
    private final MessageId<Long> messageId;

    public IdLongStreamMessage(R data) {
        this(data, null);
    }

    public IdLongStreamMessage(R data, MessageId<Long> messageId) {
        this.data = data;
        this.messageId = messageId;
    }

    @Override
    public MessageId<Long> getId() {
        return messageId;
    }

    @Override
    public R gerValue() {
        return this.data;
    }

}

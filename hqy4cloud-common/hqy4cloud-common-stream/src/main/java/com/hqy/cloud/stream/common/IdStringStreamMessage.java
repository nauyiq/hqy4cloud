package com.hqy.cloud.stream.common;

import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamMessage;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/22
 */
public abstract class IdStringStreamMessage<R> implements StreamMessage<String, R> {

    private final R data;
    private final MessageId<String> messageId;

    public IdStringStreamMessage(R data) {
        this(data, null);
    }

    public IdStringStreamMessage(R data, MessageId<String> messageId) {
        this.data = data;
        this.messageId = messageId;
    }

    @Override
    public MessageId<String> getId() {
        return messageId;
    }

    @Override
    public R gerValue() {
        return this.data;
    }
}

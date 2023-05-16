package com.hqy.mq.rocketmq.server;

import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.server.Consumer;
import com.hqy.mq.common.server.support.AbstractMqListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/13 13:23
 */
public abstract class RocketmqListener<T extends MessageModel> extends AbstractMqListener<T> implements RocketMQListener<T> {

    public RocketmqListener(Consumer<T> consumer) {
        super(consumer);
    }

    @Override
    public void onMessage(T message) {
        this.notify(message);
    }
}

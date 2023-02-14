package com.hqy.coll.rocketmq.server;

import com.hqy.base.common.base.lang.exception.MessageQueueException;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.server.support.AbstractConsumer;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/13 13:39
 */
@Slf4j
@Component
public class TestRocketmqConsumer extends AbstractConsumer<TestRocketmqMessage> {

    public TestRocketmqConsumer() {
        super(MessageQueue.ROCKETMQ);
    }

    @Override
    public void consumption(TestRocketmqMessage message) throws MessageQueueException {
        log.info("Do consumption message: {}.", JsonUtil.toJson(message));
    }

    @Override
    protected void doCallback(TestRocketmqMessage message, Throwable cause) {
        log.error("Do callback message: {}.", JsonUtil.toJson(message));
    }
}

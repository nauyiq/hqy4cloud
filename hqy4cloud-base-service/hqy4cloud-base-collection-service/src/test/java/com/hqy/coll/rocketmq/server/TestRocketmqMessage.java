package com.hqy.coll.rocketmq.server;

import cn.hutool.core.lang.UUID;
import com.hqy.mq.common.bind.MessageModel;
import com.hqy.mq.common.bind.MessageParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.rocketmq.spring.support.RocketMQHeaders;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/13 13:38
 */
@Data
@AllArgsConstructor
public class TestRocketmqMessage implements MessageModel {

    private String value;
    private Long timestamp;

    public TestRocketmqMessage() {
        this.value = UUID.fastUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public MessageParams getParameters() {
        MessageParams messageParams = new MessageParams("hahahahaha", "rocketmq-producer-group-test");
        messageParams.setParameter(RocketMQHeaders.DELAY, "3");
        return messageParams;

    }
}

package com.hqy.cloud.mq.rabbit.lang;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.common.AbstractStreamMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.Map;

/**
 * Rabbit 消息类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/27
 */
public class RabbitMessage extends AbstractStreamMessage<String, Object> {

    /**
     * 路由键
     */
    private final String routingKey;

    /**
     * 交换机
     */
    private final String exchange;

    /**
     * rabbit correlationData
     */
    private final CorrelationData correlationData;

    public RabbitMessage(String routingKey, String exchange, Object value) {
        super(null, value);
        this.routingKey = routingKey;
        this.exchange = exchange;
        this.correlationData = new CorrelationData();
    }

    public RabbitMessage(String routingKey, String exchange, MessageId<String> messageId, Object value) {
        super(messageId, value);
        this.routingKey = routingKey;
        this.exchange = exchange;
        this.correlationData = new CorrelationData();
    }

    public RabbitMessage(String routingKey, String exchange, Object value, CorrelationData correlationData) {
        super(null, value);
        this.routingKey = routingKey;
        this.exchange = exchange;
        this.correlationData = correlationData;
    }


    @Override
    public String getTopic() {
        return this.exchange + StringConstants.Symbol.UNION + this.exchange;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }

    public CorrelationData getCorrelationData() {
        return correlationData;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getExchange() {
        return exchange;
    }
}

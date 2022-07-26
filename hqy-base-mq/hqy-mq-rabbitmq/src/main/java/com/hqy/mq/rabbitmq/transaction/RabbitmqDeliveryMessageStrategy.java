package com.hqy.mq.rabbitmq.transaction;

import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.mq.common.transaction.entity.CommonMessageRecord;
import com.hqy.mq.common.transaction.stategy.AbstractRabbitMqDeliveryMessageAdaptor;
import com.hqy.mq.rabbitmq.RabbitmqProcessor;
import com.hqy.mq.rabbitmq.config.RabbitTransactionMessageRecordConfiguration;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/20 17:38
 */
@Slf4j
@Component
public class RabbitmqDeliveryMessageStrategy extends AbstractRabbitMqDeliveryMessageAdaptor {

    @Override
    public boolean deliveryMessage(CommonMessageRecord commonMessageRecord) {
        if (Objects.isNull(commonMessageRecord)) {
            log.warn("RabbitmqDeliveryMessageStrategy.deliveryMessage failure, payload is null.");
            return false;
        }

        String payload = JsonUtil.toJson(commonMessageRecord);
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Delivery message to rabbitmq, exchange:{}, payload:{}", RabbitTransactionMessageRecordConfiguration.EXCHANGE, payload);
        }

        CorrelationData data = new CorrelationData(commonMessageRecord.getMessageId());
        RabbitmqProcessor.getInstance().sendMessage(RabbitTransactionMessageRecordConfiguration.EXCHANGE, "", payload, data);
        return true;
    }
}

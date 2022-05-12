package com.hqy.mq.kafka.transaction;

import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.mq.common.transaction.entity.CommonMessageRecord;
import com.hqy.mq.common.transaction.stategy.AbstractKafkaDeliveryMessageAdaptor;
import com.hqy.mq.kafka.config.KafkaTransactionalInitialConfiguration;
import com.hqy.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/12 16:58
 */
@Component
public class KafkaDeliveryMessageStrategy extends AbstractKafkaDeliveryMessageAdaptor {

    private static final Logger log = LoggerFactory.getLogger(KafkaDeliveryMessageStrategy.class);

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public boolean deliveryMessage(CommonMessageRecord commonMessageRecord) {
        if (Objects.isNull(commonMessageRecord)) {
            log.warn("KafkaDeliveryMessageStrategy.deliveryMessage failure, payload is null.");
            return false;
        }

        String payload = JsonUtil.toJson(commonMessageRecord);
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Delivery message to kafka, topic:{}, payload:{}", KafkaTransactionalInitialConfiguration.TRANSACTIONAL_TOPIC, payload);
        }

        kafkaTemplate.send(KafkaTransactionalInitialConfiguration.TRANSACTIONAL_TOPIC, payload).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable e) {
                log.error(e.getMessage(), e);
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("delivery kafka success, topic:{}, partition:{}, offset:{}",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });


        return false;
    }
}

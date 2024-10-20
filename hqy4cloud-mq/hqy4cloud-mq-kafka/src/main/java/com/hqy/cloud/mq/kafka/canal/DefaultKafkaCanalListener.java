package com.hqy.cloud.mq.kafka.canal;

import com.hqy.cloud.canal.core.CanalGlue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 15:59
 */
@Slf4j
public class DefaultKafkaCanalListener implements KafkaCanalListener {
    private final CanalGlue canalGlue;

    public DefaultKafkaCanalListener(CanalGlue canalGlue) {
        this.canalGlue = canalGlue;
    }

    @Override
    public void onMessage(@Nonnull List<ConsumerRecord<String, String>> consumerRecords, @Nonnull Acknowledgment acknowledgment) {
        if (CollectionUtils.isEmpty(consumerRecords)) {
            return;
        }
        log.info("canal listener message size: {}.", consumerRecords.size());
        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            String value = consumerRecord.value();
            try {
                canalGlue.process(value);
            } catch (Throwable cause) {
                log.error("Failed execute to consume canal message = {}.", value, cause);
            }
        }
        acknowledgment.acknowledge();
    }

}

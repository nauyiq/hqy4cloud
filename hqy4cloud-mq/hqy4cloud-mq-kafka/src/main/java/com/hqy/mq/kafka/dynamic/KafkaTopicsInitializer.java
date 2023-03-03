package com.hqy.mq.kafka.dynamic;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * kafka主题初始化器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 16:50
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaTopicsInitializer implements SmartInitializingSingleton {

    private final KafkaTopicsProperties topicProperties;
    private final KafkaProperties properties;

    @Override
    public void afterSingletonsInstantiated() {
        log.info("Kafka dynamic create topics by configuration.");
        declareKafkaTopics();
    }

    private void declareKafkaTopics() {
        List<Topics> topics = topicProperties.getTopics();
        if (CollectionUtils.isNotEmpty(topics)) {
            AdminClient client = AdminClient.create(properties.buildAdminProperties());
            if (Objects.isNull(client)) {
                log.warn("Failed execute to declare kafka topics, AdminClient is null.");
                return;
            }
            try {
                List<NewTopic> newTopics = topics.stream().map(this::buildTopic).collect(Collectors.toList());
                client.createTopics(newTopics);
                log.info("Dynamic create kafka topics success, topics: {}.", JsonUtil.toJson(topics));
            } catch (Throwable cause) {
                log.error(cause.getMessage(), cause);
            }

        } else {
            log.info("Kafka topics in empty by configuration.");
        }

    }

    private NewTopic buildTopic(Topics metadata) {
        String name = metadata.getName();
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Topic name should not be empty.");
        }
        Integer partition = metadata.getPartition();
        Integer replicationFactor = metadata.getReplications();
        partition = partition == null ? 1 : partition;
        replicationFactor = replicationFactor == null ? 1 : replicationFactor;
        return new NewTopic(name, partition, Convert.toShort(replicationFactor));
    }


}

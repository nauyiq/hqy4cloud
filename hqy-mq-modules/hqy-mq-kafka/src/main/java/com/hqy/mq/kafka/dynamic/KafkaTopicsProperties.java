package com.hqy.mq.kafka.dynamic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 16:47
 */
@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaTopicsProperties {

    private List<TopicMetadata> topics;


}

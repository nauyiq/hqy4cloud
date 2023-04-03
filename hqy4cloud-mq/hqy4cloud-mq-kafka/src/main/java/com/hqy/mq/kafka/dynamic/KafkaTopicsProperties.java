package com.hqy.mq.kafka.dynamic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 16:47
 */
@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "kafka.config", ignoreInvalidFields = true)
public class KafkaTopicsProperties {

    private List<Topics> topics;


}

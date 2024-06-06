package com.hqy.cloud.mq.rabbit.dynamic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 9:46
 */
@Data
@ConfigurationProperties(prefix = "hqy4cloud.spring.rabbitmq")
public class RabbitModuleProperties {

    private List<RabbitMetadata> modules;
}

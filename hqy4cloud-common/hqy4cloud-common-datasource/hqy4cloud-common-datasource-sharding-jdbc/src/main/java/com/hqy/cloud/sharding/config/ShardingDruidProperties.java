package com.hqy.cloud.sharding.config;

import com.hqy4cloud.core.DruidProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/30 9:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.shardingsphere.datasource.druid")
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
public class ShardingDruidProperties extends DruidProperties {


}

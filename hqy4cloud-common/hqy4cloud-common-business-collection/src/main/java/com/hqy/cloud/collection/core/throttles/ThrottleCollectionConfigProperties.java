package com.hqy.cloud.collection.core.throttles;

import com.hqy.cloud.collection.core.CollectionConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/3
 */
@ConfigurationProperties(prefix = "hqy4cloud.collection.throttle")
public class ThrottleCollectionConfigProperties extends CollectionConfig {
}

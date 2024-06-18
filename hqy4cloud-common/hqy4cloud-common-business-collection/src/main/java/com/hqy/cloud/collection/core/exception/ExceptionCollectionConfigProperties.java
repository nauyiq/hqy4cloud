package com.hqy.cloud.collection.core.exception;

import com.hqy.cloud.collection.core.CollectionConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 业务发生异常的采集配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/3
 */
@ConfigurationProperties(prefix = "hqy4cloud.collection.exception")
public class ExceptionCollectionConfigProperties extends CollectionConfig {



}

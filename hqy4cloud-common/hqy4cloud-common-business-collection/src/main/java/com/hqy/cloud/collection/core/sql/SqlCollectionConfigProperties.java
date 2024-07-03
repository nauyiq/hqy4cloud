package com.hqy.cloud.collection.core.sql;

import com.hqy.cloud.collection.core.CollectionConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/3
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "hqy4cloud.collection.sql")
public class SqlCollectionConfigProperties extends CollectionConfig {

    private int maxLength;



}

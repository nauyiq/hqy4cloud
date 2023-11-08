package com.hqy.cloud.elasticsearch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/27 17:40
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchAutoConfiguration extends ElasticsearchConfiguration {

    private final ElasticsearchProperties properties;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(properties.getUris().toArray(new String[0]))
                .withConnectTimeout(properties.getConnectionTimeout())
                .withSocketTimeout(properties.getSocketTimeout())
                .withBasicAuth(properties.getUsername(), properties.getPassword())
                .build();
    }
}

package com.hqy.cloud.elasticsearch.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/27 17:40
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "easy-es.enabled", havingValue = "false")
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties properties) {
        List<String> uris = properties.getUris();
        AssertUtil.notEmpty(uris, "Failed execute to create es client, es connection uris should not be empty.");

        // 设置连接es connection
        List<HttpHost> hosts = uris.stream().map(this::uriConvert2Host).collect(Collectors.toList());
        RestClientBuilder builder = RestClient.builder(ArrayUtil.toArray(hosts, HttpHost.class));
        // 配置连接数据
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout((int) properties.getConnectionTimeout().getSeconds());
            requestConfigBuilder.setSocketTimeout((int) properties.getSocketTimeout().getSeconds());
            requestConfigBuilder.setConnectionRequestTimeout((int) properties.getConnectionTimeout().getSeconds());
            return requestConfigBuilder;
        });
        return new RestHighLevelClient(builder);
    }

    private HttpHost uriConvert2Host(String uri) throws IllegalArgumentException {
        try {
            String[] hosts = uri.split(StrUtil.COLON);
            String host = hosts[0];
            Integer port = Convert.toInt(hosts[1]);
            return new HttpHost(host, port);
        } catch (Throwable cause) {
            throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

    }



}

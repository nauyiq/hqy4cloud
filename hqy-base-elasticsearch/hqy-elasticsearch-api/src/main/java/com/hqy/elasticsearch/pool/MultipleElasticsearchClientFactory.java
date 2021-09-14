/*
package com.hqy.elasticsearch.pool;

import com.google.common.net.HostAndPort;
import com.hqy.common.swticher.CommonSwitcher;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

*/
/**
 * 在使用elasticsearch官网提供的Java High Level Rest Client来对es进行操作时，发现客户端API中没有连接池的概念
 * 利用 apache common pool, 对es客户端进行池化服用
 * 从官网可知， 新版本建议使用高级客户端， 即RestHighLevelClient
 * @author qy
 * @create 2021/9/9 23:21
 *//*

public class MultipleElasticsearchClientFactory
        extends BasePooledObjectFactory<RestHighLevelClient> {

    private static final Logger log = LoggerFactory.getLogger(MultipleElasticsearchClientFactory.class);

    private final AtomicReference<Set<HostAndPort>> nodes;

    public MultipleElasticsearchClientFactory(AtomicReference<Set<HostAndPort>> nodes) {
        this.nodes = nodes;
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("[MultipleElasticsearchClientFactory] init, nodes:{}.", nodes);
        }
    }


    @Override
    public RestHighLevelClient create() throws Exception {
        Set<HostAndPort> hostAndPorts = nodes.get();
        if (CollectionUtils.isEmpty(hostAndPorts)) {
            throw new IllegalAccessException("初始化es连接池工厂异常, nodes is empty.");
        }
        RestClientBuilder restClientBuilder = RestClient.
                builder(hostAndPorts.stream().map(e -> new HttpHost(e.getHost(), e.getPort())).distinct().toArray(HttpHost[]::new));
        return new RestHighLevelClient(restClientBuilder);
    }

    @Override
    public PooledObject<RestHighLevelClient> wrap(RestHighLevelClient restHighLevelClient) {
        return new DefaultPooledObject<>(restHighLevelClient);
    }

    @Override
    public void destroyObject(PooledObject<RestHighLevelClient> p) {
        RestHighLevelClient client = p.getObject();
        try {
            if (client != null && client.ping(RequestOptions.DEFAULT)) {
                client.close();
            }
        } catch (IOException e) {
            log.error("### destroy RestHighLevelClient failure.");
        }
    }

    @Override
    public boolean validateObject(PooledObject<RestHighLevelClient> p) {
        RestHighLevelClient client = p.getObject();
        try {
            return client.ping(RequestOptions.DEFAULT);
        } catch (IOException e) {
            return false;
        }

    }
}
*/

package com.hqy.gateway.nacos;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * nacos配置自动更新 监听RefreshRoutesEvent
 * @author qiyuan.hong
 * @date  2021-09-17 10:24
 */
@Slf4j
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

    private static final String GATEWAY_ROUTE_DATA_ID = "gateway-route";

    private static final String GATEWAY_ROUTE_GROUP_ID = "DEV_GROUP";

    private final ApplicationEventPublisher publisher;

    private final NacosConfigManager nacosConfigManager;

    public NacosRouteDefinitionRepository(ApplicationEventPublisher publisher, NacosConfigProperties nacosConfigProperties) {
        this.publisher = publisher;
        nacosConfigManager = new NacosConfigManager(nacosConfigProperties);
        addListener();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            String config = nacosConfigManager.getConfigService().getConfig(GATEWAY_ROUTE_DATA_ID, GATEWAY_ROUTE_GROUP_ID, 5000);
            List<RouteDefinition> routeDefinitions;
            if (StringUtils.isNotBlank(config)) {
                routeDefinitions = JsonUtil.toList(config, RouteDefinition.class);
            } else {
                routeDefinitions = new ArrayList<>();
            }
            return Flux.fromIterable(routeDefinitions);
        } catch (NacosException e) {
            log.error(e.getErrMsg(), e);
        }
        return  Flux.fromIterable(CollUtil.newArrayList());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    /**
     * 添加Nacos监听
     */
    private void addListener() {
        try {
            nacosConfigManager.getConfigService().addListener(GATEWAY_ROUTE_DATA_ID, GATEWAY_ROUTE_GROUP_ID, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                }
            });
        } catch (NacosException e) {
            log.error(e.getErrMsg(), e);
        }
    }
}

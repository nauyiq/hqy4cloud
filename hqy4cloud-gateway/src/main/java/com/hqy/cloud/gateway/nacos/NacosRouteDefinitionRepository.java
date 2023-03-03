package com.hqy.cloud.gateway.nacos;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.util.JsonUtil;
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

    private final String gatewayRouteDataId;

    private final String gatewayRouteGroup;

    private final ApplicationEventPublisher publisher;

    private final NacosConfigManager nacosConfigManager;

    public NacosRouteDefinitionRepository(String gatewayRouteDataId, String gatewayRouteGroup, ApplicationEventPublisher publisher, NacosConfigProperties nacosConfigProperties) {
        this.publisher = publisher;
        nacosConfigManager = new NacosConfigManager(nacosConfigProperties);
        this.gatewayRouteDataId = gatewayRouteDataId;
        this.gatewayRouteGroup = gatewayRouteGroup;

        addListener();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            String config = nacosConfigManager.getConfigService().getConfig(gatewayRouteDataId, gatewayRouteGroup, NumberConstants.ONE_SECONDS_4MILLISECONDS * 5);
            List<RouteDefinition> routeDefinitions = new ArrayList<>();
            if (StringUtils.isNotBlank(config)) {
                try {
                    routeDefinitions = JsonUtil.toList(config, RouteDefinition.class);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
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
            nacosConfigManager.getConfigService().addListener(gatewayRouteDataId, gatewayRouteGroup, new Listener() {
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

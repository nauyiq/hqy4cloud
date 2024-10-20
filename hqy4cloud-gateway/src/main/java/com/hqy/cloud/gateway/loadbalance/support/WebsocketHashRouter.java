package com.hqy.cloud.gateway.loadbalance.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.gateway.loadbalance.WebsocketRouter;
import com.hqy.cloud.socket.cluster.HashRouterService;
import com.hqy.cloud.socket.model.SocketServerMetadata;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

import static com.hqy.cloud.socket.SocketConstants.SOCKET_SERVER_DEPLOY_METADATA_KEY;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26
 */
@Slf4j
@RequiredArgsConstructor
public class WebsocketHashRouter implements WebsocketRouter {

    @Override
    public ServiceInstance router(String serviceName, int hash, List<ServiceInstance> instances) {
        HashRouterService hashRouterService = SpringUtil.getBean(HashRouterService.class);
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("WebsocketHashRouter service instances is empty.");
            return null;
        }
        // hash值对应的路由地址.
        String hashRouterServiceAddress = hashRouterService.getAddress(serviceName, hash);
        if (StringUtils.isBlank(hashRouterServiceAddress)) {
            return null;
        }

        if (instances.size() == 1) {
            return instances.getFirst();
        } else {
            for (ServiceInstance instance : instances) {
                String hashAddress = getInstanceHashAddress(instance, getSocketPort(instance));
                if (hashAddress.equals(hashRouterServiceAddress)) {
                    return instance;
                }
            }
            return null;
        }

    }

    private String getInstanceHashAddress(ServiceInstance instance, int socketPort) {
        return instance.getHost() + StrUtil.COLON + socketPort;
    }

    private int getSocketPort(ServiceInstance serviceInstance) {
        String metadataStr = serviceInstance.getMetadata().get(SOCKET_SERVER_DEPLOY_METADATA_KEY);
        if (StringUtils.isNotBlank(metadataStr)) {
            SocketServerMetadata metadata = JsonUtil.toBean(metadataStr, SocketServerMetadata.class);
            return metadata.getPort();
        }
        return 0;
    }


}

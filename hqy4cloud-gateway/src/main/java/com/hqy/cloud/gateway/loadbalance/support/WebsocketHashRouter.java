package com.hqy.cloud.gateway.loadbalance.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.gateway.loadbalance.WebsocketRouter;
import com.hqy.cloud.socket.model.SocketServerMetadata;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.foundation.router.HashRouterService;
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
//    private final HashRouterService hashRouterService;

    @Override
    public ServiceInstance router(String serviceName, int hash, List<ServiceInstance> instances) {
        HashRouterService hashRouterService = SpringContextHolder.getBean(HashRouterService.class);
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
            ServiceInstance instance = instances.get(0);
            /*int socketPort = getSocketPort(instance);
            if (socketPort != 0) {
                String instanceHashAddress = getInstanceHashAddress(instance, socketPort);
                if (!hashRouterServiceAddress.equals(instanceHashAddress)) {
                    // Update hash router
                    updateHashRouter(hash, instanceHashAddress);
                }
            }*/
            return instance;
        } else {
                /*// 随机选取一个
                ServiceInstance chooseInstance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
                String instanceHashAddress = getInstanceHashAddress(chooseInstance, getSocketPort(chooseInstance));
                updateHashRouter(hash, instanceHashAddress);
                return chooseInstance;*/
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

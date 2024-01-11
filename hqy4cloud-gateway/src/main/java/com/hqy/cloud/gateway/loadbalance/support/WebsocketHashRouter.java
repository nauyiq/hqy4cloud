package com.hqy.cloud.gateway.loadbalance.support;

import com.hqy.cloud.foundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.cloud.gateway.loadbalance.WebsocketRouter;
import com.hqy.foundation.util.SocketHashFactorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26
 */
@Slf4j
public class WebsocketHashRouter implements WebsocketRouter {

    @Override
    public ServiceInstance router(String serviceName, int hash, List<ServiceInstance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("WebsocketHashRouter service instances is empty.");
            return null;
        }







        if (instances.size() == 1) {
            ServiceInstance instance = instances.get(0);
            LoadBalanceHashFactorManager.registry(serviceName, hash, SocketHashFactorUtils.genHashFactor(instance.getHost(), instance.getPort()));
            return instance;
        } else {
            String hashFactor = LoadBalanceHashFactorManager.queryHashFactor(serviceName, hash);
            ServiceInstance router = null;
            for (ServiceInstance instance : instances) {
                String thisHashFactor = SocketHashFactorUtils.genHashFactor(instance.getHost(), instance.getPort());
                if (hashFactor.equals(thisHashFactor)) {
                    router = instance;
                }
            }

            if (router == null) {
                //re choose
                int i = hash % instances.size();
                router = instances.get(i);
                LoadBalanceHashFactorManager.registry(serviceName, hash, SocketHashFactorUtils.genHashFactor(router.getHost(), router.getPort()));
            }

            return router;
        }

    }
}

package com.hqy.cloud.gateway.loadbalance.support;

import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.hqy.cloud.gateway.loadbalance.LoadBalancer;
import com.hqy.cloud.gateway.loadbalance.ServiceInstanceLoadBalancer;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网关负载均衡策略上下文类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/28 10:15
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class GatewayLoadBalanceStrategyContext {

    private static final Map<String, ServiceInstanceLoadBalancer> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static ServiceInstanceLoadBalancer getLoadBalanceStrategy(LoadBalance loadBalance) {
        try {
            String scheme = loadBalance.scheme;
            if (INSTANCE_MAP.containsKey(scheme)) {
                return INSTANCE_MAP.get(scheme);
            }
            Class<? extends LoadBalancer> clazz = loadBalance.clazz;
            ServiceInstanceLoadBalancer serviceInstanceLoadBalancer = (ServiceInstanceLoadBalancer) clazz.newInstance();
            INSTANCE_MAP.put(scheme, serviceInstanceLoadBalancer);
            return serviceInstanceLoadBalancer;
        } catch (Exception e) {
            log.error("@@@ 没有找到相应的负载均衡策略, 请检查参数!");
            return null;

        }
    }

    public static NacosServiceInstance copy(int port, ServiceInstance serviceInstance) {
        AssertUtil.notNull(serviceInstance, "ServiceInstance should not be null.");
        NacosServiceInstance nacosServiceInstance = new NacosServiceInstance();
        nacosServiceInstance.setHost(serviceInstance.getHost());
        nacosServiceInstance.setServiceId(serviceInstance.getServiceId());
        nacosServiceInstance.setMetadata(serviceInstance.getMetadata());
        nacosServiceInstance.setPort(port);
        nacosServiceInstance.setSecure(serviceInstance.isSecure());
        return nacosServiceInstance;
    }


    public enum LoadBalance {

        /**
         * 根据hash值进行负载均衡
         */
        WS_HASH("hash", WebSocketHashLoadBalanceStrategy.class),


        ;

        public final String scheme;

        public final Class<? extends ServiceInstanceLoadBalancer> clazz;

        LoadBalance(String scheme, Class<? extends ServiceInstanceLoadBalancer> clazz) {
            this.scheme = scheme;
            this.clazz = clazz;
        }

        public static LoadBalance getLoadBalance(String scheme) {
            for (LoadBalance value : LoadBalance.values()) {
                if (value.scheme.equals(scheme)) {
                    return value;
                }
            }
            throw new IllegalStateException("@@@ 没有找到相应的负载均衡策略, 请检查参数!");
        }

    }

}

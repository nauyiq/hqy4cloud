package com.hqy.gateway.route;

import lombok.extern.slf4j.Slf4j;

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


    public enum LoadBalance{

        /**
         * 根据hash值进行负载均衡
         */
        SOCKET("socket", SocketHashLoadBalanceStrategy.class),


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

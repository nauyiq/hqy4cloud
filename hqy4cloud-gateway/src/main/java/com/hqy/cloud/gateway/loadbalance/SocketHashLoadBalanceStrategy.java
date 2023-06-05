
package com.hqy.cloud.gateway.loadbalance;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.foundation.util.SocketHashFactorUtils;
import com.hqy.cloud.foundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.nacos.node.Metadata;
import com.hqy.cloud.rpc.nacos.utils.NacosInstanceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;


/**
 * 基于socket hash值进行负载的策略<br>
 * 通常用于socket.io项目注册的时候配置了集群启动 因此某个客户端必须有状态的进行TCP的长连接握手. 基于hash值进行标记连接.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/25 17:41
 */
@SuppressWarnings("deprecation")
public class SocketHashLoadBalanceStrategy extends ServiceInstanceLoadBalancer {

    private static final Logger log = LoggerFactory.getLogger(SocketHashLoadBalanceStrategy.class);

    @Override
    public Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange, DiscoveryClient discoveryClient) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        if (Objects.isNull(url)) {
            throw new IllegalStateException("@@@ 转发异常, 获取不到对应的实例列表");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(url.getHost());
        if (CollectionUtils.isEmpty(instances)) {
            throw new NotFoundException("@@@ 转发异常, 获取不到对应的实例列表");
        }
        return Mono.just(getInstanceResponse(instances, exchange));
    }


    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, ServerWebExchange exchange) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("@@@ 采用hash负载均衡, 当前服务的实例数 -> {}", instances.size());
        }

        ServerHttpRequest request = exchange.getRequest();
        //获取请求中的hash值
        List<String> thisHash = request.getQueryParams().get(StringConstants.Auth.SOCKET_MULTI_PARAM_KEY);
        ServiceInstance instance = null;
        if (CollectionUtils.isEmpty(thisHash)) {
            //如果请求的hash值为空 返回一个无状态的实例
            ServiceInstance serviceInstance;
            if (instances.size() == 1) {
                serviceInstance = instances.get(0);
            } else {
                serviceInstance = instances.get(new Random().nextInt() % instances.size());
            }
            //获取socket.io项目端口
            int socketPort = getSocketPort(serviceInstance);
            instance = copy(socketPort, serviceInstance);

        } else {
            String hash = thisHash.get(0);
            String module = instances.get(0).getServiceId();
            String queryHashFactor = LoadBalanceHashFactorManager.queryHashFactor(module, Integer.parseInt(hash));
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("@@@ 采用hash值进行负载, hash:{}, module:{}, queryHashFactor:{}", hash, module, queryHashFactor);
            }
            for (ServiceInstance serviceInstance : instances) {
                //获取socket.io项目端口
                int socketPort = getSocketPort(serviceInstance);
                String hashFactor = SocketHashFactorUtils.genHashFactor(serviceInstance.getHost(), socketPort);
                if (queryHashFactor.equals(hashFactor)) {
                    instance = copy(socketPort, serviceInstance);
                    break;
                }
            }
        }
        return new DefaultResponse(instance);
    }

    private int getSocketPort(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        if (metadata == null || metadata.isEmpty()) {
            //Gets socket.io server port failure, should not find it from nacos metadata.
            return 0;
        }
        try {
            Metadata nacosMetadata = NacosInstanceUtils.toMetadataFromMap(metadata);
            if (nacosMetadata == null) {
                return 0;
            }
            RPCServerAddress rpcServerAddress = nacosMetadata.getRpcServerAddress();
            return rpcServerAddress.getPort();
        } catch (Throwable t) {
            log.warn("Failed execute to map convert to Metadata. map:{}.", JsonUtil.toJson(metadata));
            return 0;
        }
    }
}


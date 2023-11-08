
package com.hqy.cloud.gateway.loadbalance.support;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.common.route.support.SocketPortRouterManager;
import com.hqy.cloud.gateway.loadbalance.ServiceInstanceLoadBalancer;
import com.hqy.cloud.gateway.loadbalance.WebsocketRouter;
import com.hqy.foundation.util.SocketHashFactorUtils;
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
import java.util.Objects;
import java.util.Random;

import static com.hqy.cloud.common.base.config.ConfigConstants.SOCKET_MULTI_PARAM_KEY;
import static com.hqy.cloud.gateway.loadbalance.support.GatewayLoadBalanceStrategyContext.copy;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;


/**
 * 基于socket hash值进行负载的策略<br>
 * 通常用于socket.io项目注册的时候配置了集群启动 因此某个客户端必须有状态的进行TCP的长连接握手. 基于hash值进行标记连接.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/25 17:41
 */
public class WebSocketHashLoadBalanceStrategy extends ServiceInstanceLoadBalancer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHashLoadBalanceStrategy.class);
    private final WebsocketRouter router = new WebsocketHashRouter();


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
        List<String> thisHash = request.getQueryParams().get(SOCKET_MULTI_PARAM_KEY);
        ServiceInstance instance;
        if (CollectionUtils.isEmpty(thisHash)) {
            //如果请求的hash值为空 返回一个无状态的实例
            instance = instances.size() == 1 ?  instances.get(0) : instances.get(new Random().nextInt() % instances.size());
        } else {
            String hash = thisHash.get(0);
            String module = instances.get(0).getServiceId();
            instance = this.router.router(module, Integer.parseInt(hash), instances);
        }
        int socketPort = getSocketPort(instance);
        instance = copy(socketPort, instance);
        return new DefaultResponse(instance);
    }

    private int getSocketPort(ServiceInstance serviceInstance) {
        //从Socket端口路由器中获取服务端口
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        String serviceName = serviceInstance.getServiceId();
        return SocketPortRouterManager.getPort(serviceName, SocketHashFactorUtils.genHashFactor(host, port));
    }
}


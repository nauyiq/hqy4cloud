package com.hqy.cloud.gateway.filter;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.gateway.Constants;
import com.hqy.cloud.gateway.loadbalance.GatewayLoadBalanceStrategyContext;
import com.hqy.cloud.gateway.loadbalance.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;

import static org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools.reconstructURI;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;

/**
 * 负载均衡过滤器 基于ReactiveLoadBalancerClientFilter.java
 * 增加自定义负载策略
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/25 16:52
 */
@Component
public class ReactiveCustomerLoadBalancerClientFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ReactiveCustomerLoadBalancerClientFilter.class);
    @Resource
    private GatewayLoadBalancerProperties properties;

    @Resource
    private DiscoveryClient discoveryClient;

    public ReactiveCustomerLoadBalancerClientFilter(DiscoveryClient discoveryClient, GatewayLoadBalancerProperties properties) {
        this.properties = properties;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (CommonSwitcher.ENABLE_CUSTOMER_GATEWAY_LOAD_BALANCE.isOff()) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("@@@ 开关-是否采用自定义的网关负载均衡策略 -> isOff");
            }
            //直接执行下一条责任链
            return chain.filter(exchange);
        }

        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || "lb".equals(url.getScheme()) || "lb".equals(schemePrefix)) {
            //直接执行下一条责任链
            return chain.filter(exchange);
        }

        ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace(ReactiveCustomerLoadBalancerClientFilter.class.getSimpleName()
                    + " url before: " + url);
        }

        return choose(exchange, url).doOnNext(response -> {
            if (!response.hasServer()) {
                throw NotFoundException.create(properties.isUse404(),
                        "Unable to find instance for " + url.getHost());
            }
            ServiceInstance retrievedInstance = response.getServer();
            URI uri = exchange.getRequest().getURI();
            // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
            // if the loadbalancer doesn't provide one.
            String overrideScheme = retrievedInstance.isSecure() ? "https" : "http";
            if (schemePrefix != null) {
                overrideScheme = url.getScheme();
            }

            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(
                    retrievedInstance, overrideScheme);

            URI requestUrl = reconstructURI(serviceInstance, uri);

            if (log.isTraceEnabled()) {
                log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
            }
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);

        }).then(chain.filter(exchange));

    }

    @SuppressWarnings("deprecation")
    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange, URI uri) {
        GatewayLoadBalanceStrategyContext.LoadBalance loadBalance =
                GatewayLoadBalanceStrategyContext.LoadBalance.getLoadBalance(uri.getScheme());
        //选择负载均衡.
        LoadBalancer<ServiceInstance> loadBalancer = GatewayLoadBalanceStrategyContext.getLoadBalanceStrategy(loadBalance);
        if (loadBalancer == null) {
            throw new NotFoundException("No loadbalancer available for " + uri.getScheme());
        }
        return loadBalancer.choose(exchange, discoveryClient);
    }


    @Override
    public int getOrder() {
        //10149 刚好在原生过滤器ReactiveLoadBalancerClientFilter前面
        return Constants.LOAD_BALANCER_FILTER_ORDER;
    }
}

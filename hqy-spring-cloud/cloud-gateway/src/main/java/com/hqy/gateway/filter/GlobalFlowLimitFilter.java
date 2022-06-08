package com.hqy.gateway.filter;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.foundation.limit.RoutingRule;
import com.hqy.gateway.util.RoutingContext;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 流量限制过滤器 针对不同服务进行限流 防止高并发导致服务奔溃
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/8 9:48
 */
@Slf4j
@Component
public class GlobalFlowLimitFilter implements GlobalFilter, Ordered, InitializingBean {

    @Value("${spring.cloud.sentinel.enabled}")
    private boolean enableSentinel;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (enableSentinel) {
            //如果已经走sentinel流量管理 则直接放行 交给sentinel去处理流量限制
            return chain.filter(exchange);
        }

        //获取当前的流向配置器
        RoutingRule rule = RoutingContext.getRouting(exchange);
        //获取限流器




        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (enableSentinel) {
            //设置限流或降级的回调函数
            GatewayCallbackManager.setBlockHandler((serverWebExchange, throwable) ->
                    ServerResponse.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(JsonUtil.toJson(CommonResultCode.messageResponse(CommonResultCode.ILLEGAL_REQUEST_LIMITED))));

            initCustomizedApis();
            initGatewayRules();
        }
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        //sample
        ApiDefinition api1 = new ApiDefinition("some_customized_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/ahas"));
                    add(new ApiPathPredicateItem().setPattern("/product/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        //资源保护名 account-auth-service Path: /oauth2/**
        ApiDefinition api2 = new ApiDefinition("account-auth-service")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/oauth/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(api1);
        definitions.add(api2);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }


    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        /*
           resource:资源名称 可以是网关中的route名称或者用户自定义的API分组名称
           count：限流阈值
           intervalSec：统计时间窗口，单位秒 默认1秒
         */
        rules.add(new GatewayFlowRule("account-auth-service")
                .setCount(10)
                .setIntervalSec(1)
        );

        /*rules.add(new GatewayFlowRule("aliyun_route")
                .setCount(2)
                .setIntervalSec(2)
                .setBurst(2)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                )
        );*/

        /*rules.add(new GatewayFlowRule("httpbin_route")
                .setCount(10)
                .setIntervalSec(1)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
                .setMaxQueueingTimeoutMs(600)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER)
                        .setFieldName("X-Sentinel-Flag")
                )
        );*/

        /*rules.add(new GatewayFlowRule("httpbin_route")
                .setCount(1)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                        .setFieldName("pa")
                )
        );*/

        /*rules.add(new GatewayFlowRule("httpbin_route")
                .setCount(2)
                .setIntervalSec(30)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                        .setFieldName("type")
                        .setPattern("warn")
                        .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_CONTAINS)
                )
        );*/

        /*rules.add(new GatewayFlowRule("some_customized_api")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(5)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                        .setFieldName("pn")
                )
        );*/
        GatewayRuleManager.loadRules(rules);

    }



}

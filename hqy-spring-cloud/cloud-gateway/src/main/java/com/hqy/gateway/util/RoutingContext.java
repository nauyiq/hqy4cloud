package com.hqy.gateway.util;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.foundation.limit.RoutingRule;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 15:07
 */
public class RoutingContext {

    public static final Map<String, RoutingRule> ROUTING_RULE_MAP = new HashMap<>(6);


    static {
        ROUTING_RULE_MAP.put(BaseStringConstants.DEFAULT, new RoutingRule(BaseStringConstants.DEFAULT, "/") {});
        ROUTING_RULE_MAP.put(MicroServiceConstants.ACCOUNT_SERVICE,  new RoutingRule(MicroServiceConstants.ACCOUNT_SERVICE, "/oauth/**") {});
    }

    public static RoutingRule getRouting(ServerWebExchange exchange) {
        //获取当前请求命中的路由对象
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (Objects.isNull(route)) {
            return ROUTING_RULE_MAP.get(BaseStringConstants.DEFAULT);
        }
        String routeId = route.getId();
        if (ROUTING_RULE_MAP.containsKey(routeId)) {
            return ROUTING_RULE_MAP.get(routeId);
        } else {
            return ROUTING_RULE_MAP.get(BaseStringConstants.DEFAULT);
        }

    }


}

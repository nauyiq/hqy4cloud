package com.hqy.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.JsonUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.HashSet;
import java.util.Set;

/**
 * sentinel 访问超限处理器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/2 15:26
 */
//@Configuration
//@ConditionalOnProperty(prefix = "spring.cloud.sentinel", name = "enabled", havingValue = "true", matchIfMissing = true)
@Deprecated
public class SentinelOverFlowConfiguration implements InitializingBean {


    @Override
    public void afterPropertiesSet() throws Exception {
        //设置限流或降级的回调函数
        GatewayCallbackManager.setBlockHandler((serverWebExchange, throwable) ->
                ServerResponse.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(JsonUtil.toJson(CommonResultCode.messageResponse(CommonResultCode.ILLEGAL_REQUEST_LIMITED))));

//        initCustomizedApis();
//        initGatewayRules();
    }




}

package com.hqy.access.flow;

import com.hqy.access.flow.strategy.RequestLimiterStrategy;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.foundation.limit.LimitAlgorithm;
import com.hqy.foundation.limit.RoutingRule;
import com.hqy.util.AssertUtil;

/**
 * 节流上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 14:29
 */
public class ThrottlesLimiterContext {

    private final RequestLimiterStrategy requestLimiterStrategy;

    public ThrottlesLimiterContext(RequestLimiterStrategy requestLimiterStrategy) {
        this.requestLimiterStrategy = requestLimiterStrategy;
    }






    public static RequestLimiterStrategy getLimiter(RoutingRule rule) {
        AssertUtil.notNull(rule, "Current routing rule is null, check configuration.");

        LimitAlgorithm strategy = rule.strategy();
        if (CommonSwitcher.ENABLE_CLUSTER_LIMITING_REQUEST_STATISTICS.isOn()) {

        }

        //TODO 待调整
        return null;
    }



}

package com.hqy.cloud.auth.flow.strategy;

import com.hqy.cloud.auth.flow.FlowLimitConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/17 14:38
 */
@Slf4j
public abstract class AbstractLimiter implements ResourceLimitStrategy {

    private FlowLimitConfig config;

    public AbstractLimiter(FlowLimitConfig config) {
        this.config = config;
    }

    public FlowLimitConfig getConfig() {
        return config;
    }

    public void setConfig(FlowLimitConfig config) {
        this.config = config;
    }
}

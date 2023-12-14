package com.hqy.cloud.foundation.limiter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/17 14:38
 */
@Slf4j
public abstract class AbstractLimiter implements Limiter {

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

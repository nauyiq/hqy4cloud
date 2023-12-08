package com.hqy.cloud.actuator.filter;

import org.springframework.core.Ordered;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7 15:28
 */
public abstract class AbstractActuatorBasicFilter implements ActuatorBasicFilter {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

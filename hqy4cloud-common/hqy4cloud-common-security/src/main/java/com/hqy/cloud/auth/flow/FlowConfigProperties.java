package com.hqy.cloud.auth.flow;

import com.hqy.cloud.foundation.limiter.FlowLimitConfig;
import com.hqy.cloud.foundation.limiter.Measurement;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/17 16:39
 */
@Data
@ConfigurationProperties(prefix = "hqy4cloud.resource.limit")
public class FlowConfigProperties {

    public static final FlowLimitConfig DEFAULT_CONFIG = new FlowLimitConfig(15, Measurement.Seconds.ONE_SECONDS);
    private FlowLimitConfig getLimitConfig = DEFAULT_CONFIG;
    private FlowLimitConfig postLimitConfig = new FlowLimitConfig(10, Measurement.Seconds.ONE_SECONDS);
    private FlowLimitConfig uriLimitConfig = DEFAULT_CONFIG;


}

package com.hqy.cloud.limiter.flow;

import com.hqy.cloud.limiter.core.LimitMode;
import com.hqy.cloud.limiter.core.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流量限流配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/16 17:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowLimitConfig {

    /**
     * 限流规则
     */
    private LimitMode limitMode = LimitMode.QPS;

    /**
     * 最大访问次数
     */
    private int count;

    /**
     * 如果超限 是否对当前请求的来源进行封禁 比如ip封禁, 默认10分钟
     */
    private int blockSeconds = 10 * Measurement.Seconds.ONE_MINUTES.seconds;

    /**
     * 时间窗口 单位s
     */
    private int windows;


    public FlowLimitConfig(int count, int windows) {
         this(LimitMode.QPS, count, windows);
    }

    public FlowLimitConfig(LimitMode limitMode, int count, int windows) {
        this.limitMode = limitMode;
        this.count = count;
        this.windows = windows;
    }

    public static FlowLimitConfig of(int count) {
        return new FlowLimitConfig(count, 1);
    }

    public static FlowLimitConfig of(int count, int windows) {
        return new FlowLimitConfig(count, windows);
    }

}

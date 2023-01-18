package com.hqy.access.flow;

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
    private Measurement.Seconds windows;


    public FlowLimitConfig(int count, Measurement.Seconds windows) {
         this(LimitMode.QPS, count, windows);
    }


    public FlowLimitConfig(LimitMode limitMode, int count, Measurement.Seconds windows) {
        this.limitMode = limitMode;
        this.count = count;
        this.windows = windows;
    }
}

package com.hqy.foundation.collection;

import lombok.Data;

/**
 * 采集器配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 17:20
 */
@Data
public class CollectorConfig {

    public static final CollectorConfig DEFAULT = new CollectorConfig();

    /**
     * 采集之前检查队列长度， 队列繁忙时则不采集了
     */
    private boolean checkQueue = true;

    /**
     * 采集的频率。 发生多少次采集一次， 交给具体业务进行配置
     */
    private int collectFrequency = 100;

    /**
     * 统计限流的时间窗口长度 （单位秒）
     */
    private int rateLimitedWindowSecond = 2;

    /**
     * 时间窗口内 允许采集的最大值 防止采集过多超限
     */
    private int rateLimited = 10;


    /**
     * 是否报警通知 默认不告警
     */
    private boolean alert = false;

    public CollectorConfig() {
    }

    public CollectorConfig(int collectFrequency) {
        this.collectFrequency = collectFrequency;
    }

    public CollectorConfig(boolean checkQueue, int collectFrequency, boolean alert) {
        this.checkQueue = checkQueue;
        this.collectFrequency = collectFrequency;
        this.alert = alert;
    }
}

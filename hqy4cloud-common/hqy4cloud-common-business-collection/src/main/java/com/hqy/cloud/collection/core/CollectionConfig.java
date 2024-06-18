package com.hqy.cloud.collection.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 采集器配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11
 */
@Setter
@Getter
public class CollectionConfig {

    public static final CollectionConfig DEFAULT = new CollectionConfig();

    private boolean enabled = true;

    /**
     * 采集的频率。 发生多少次采集一次， 交给具体业务进行配置
     */
    private int frequency = 100;

    /**
     * 统计限流的时间窗口长度 （单位秒）
     */
    private int windowSize = 2;

    /**
     * 时间窗口内 允许采集的最大值 防止采集过多超限
     */
    private int count = 20;


    public CollectionConfig() {
    }

    public CollectionConfig(int frequency) {
        this.frequency = frequency;
    }

}

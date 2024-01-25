package com.hqy.cloud.common.base.lang;

import java.time.Duration;

/**
 * 常用时间常量
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/25
 */
public interface DateMeasureConstants {

    /**
     * 1分钟
     */
    Duration ONE_MINUTES = Duration.ofMillis(NumberConstants.ONE_MINUTES_4MILLISECONDS);

    /**
     * 5分钟
     */
    Duration FIVE_MINUTES = Duration.ofMillis(NumberConstants.FIVE_MINUTES_4MILLISECONDS);

    /**
     * 1小时.
     */
    Duration ONE_HOUR = Duration.ofMillis(NumberConstants.ONE_HOUR_4MILLISECONDS);




}

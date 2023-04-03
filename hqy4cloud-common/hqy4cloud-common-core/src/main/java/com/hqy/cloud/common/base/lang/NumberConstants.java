package com.hqy.cloud.common.base.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/18 15:04
 */
public interface NumberConstants {

    /**
     * 一秒 毫秒单位
     */
    long ONE_SECONDS_4MILLISECONDS = 1000;

    /**
     * 一分钟 毫秒单位
     */
    long ONE_MINUTES_4MILLISECONDS = 60 * ONE_SECONDS_4MILLISECONDS;

    /**
     * 5分钟 毫秒单位
     */
    long FIVE_MINUTES_4MILLISECONDS = 5  * ONE_MINUTES_4MILLISECONDS;

    /**
     * 10分钟 毫秒单位
     */
    long TEN_MINUTES_4MILLISECONDS = 10 * ONE_MINUTES_4MILLISECONDS;

    /**
     * 一小时 毫秒单位
     */
    long ONE_HOUR_4MILLISECONDS = ONE_MINUTES_4MILLISECONDS * 60;

    /**
     * 一天 毫秒单位
     */
    long ONE_DAY_4MILLISECONDS = ONE_HOUR_4MILLISECONDS * 24;

}

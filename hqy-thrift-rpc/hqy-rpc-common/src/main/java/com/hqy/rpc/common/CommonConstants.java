package com.hqy.rpc.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:33
 */
public interface CommonConstants {

    String REGISTRY_DELAY_NOTIFICATION_KEY = "delay-notification";

    String REGISTRY_RETRY_PERIOD_KEY = "retry.period";

    int DEFAULT_DELAY_NOTIFICATION_TIME = 5000;

    /**
     * Default value for the period of retry interval in milliseconds: 5000
     */
    int DEFAULT_REGISTRY_RETRY_PERIOD = DEFAULT_DELAY_NOTIFICATION_TIME;





}

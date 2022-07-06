package com.hqy.rpc.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:33
 */
public interface CommonConstants {

    String REGISTRY_DELAY_NOTIFICATION_KEY = "delay-notification";

    int DEFAULT_DELAY_NOTIFICATION_TIME = 5000;

    int DEFAULT_RECONNECT_TASK_TRY_COUNT = 10;

    int DEFAULT_RECONNECT_TASK_PERIOD = 1000;

    String WEIGHT = "weight";
    String WARMUP = "warmup";
    int DEFAULT_WEIGHT = 100;
    int DEFAULT_WARMUP = 5 * 60 * 1000;



}

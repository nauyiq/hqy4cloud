package com.hqy.rpc.registry;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/24 17:49
 */
public interface Constants {

    String DEV_REGISTRY_GROUP_KEY = "nacos.group";

    /**
     * Period of registry center's retry interval
     */
    String REGISTRY_RETRY_PERIOD_KEY = "retry.period";

    /**
     * Most retry times
     */
    String REGISTRY_RETRY_TIMES_KEY = "retry.times";

    /**
     * Default value for the times of retry: 3
     */
    int DEFAULT_REGISTRY_RETRY_TIMES = 3;

    /**
     * Default value for the period of retry interval in milliseconds: 5000
     */
    int DEFAULT_REGISTRY_RETRY_PERIOD = 5 * 1000;

    int DEFAULT_DELAY_EXECUTE_TIMES = 10;








}

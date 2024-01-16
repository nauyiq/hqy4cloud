package com.hqy.cloud.registry.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 9:58
 */
public interface Constants {

    /**
     * register username
     */
    String USERNAME = "username";

    /**
     * register password
     */
    String PASSWORD = "password";

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

    /**
     * default executor enable delay count.
     */
    int DEFAULT_DELAY_EXECUTE_TIMES = 10;

    /**
     * using get registry delay notification time.
     */
    String REGISTRY_DELAY_NOTIFICATION_KEY = "delay-notification";

    /**
     * using get registry cluster name.
     */
    String REGISTRY_CLUSTER_KEY = "registry-cluster";

    /**
     * delay notification time
     */
    int DEFAULT_DELAY_NOTIFICATION_TIME = 5000;

    /**
     * default cluster master lock millis
     */
    long DEFAULT_MASTER_INSTANCE_CHOOSE_LOCK_MILLIS = 5000;

    /**
     * using master lock timeout
     */
    long DEFAULT_MASTER_INSTANCE_CHOOSE_LOCK_TIMEOUT_MILLIS = 1000;

    /**
     * default instance revision
     */
    String DEFAULT_REVISION = "1.0.0";

    /**
     * default weight
     */
    int DEFAULT_WEIGHT = 100;


    String CONFIGURATION_PREFIX = "hqy4cloud.application.deploy";
    String CONFIGURATION_PREFIX_COMPONENTS = "hqy4cloud.application.deploy.components";





}

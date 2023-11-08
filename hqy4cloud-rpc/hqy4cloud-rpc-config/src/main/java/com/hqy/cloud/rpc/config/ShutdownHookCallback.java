package com.hqy.cloud.rpc.config;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 17:36
 */
public interface ShutdownHookCallback {

    /**
     * Callback execution
     *
     * @throws Throwable if met with some errors
     */
    void callback() throws Throwable;
}

package com.hqy.cloud.rpc.deploy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:10
 */
public enum DeployState {

    /**
     * Unknown state
     */
    UNKNOWN,

    /**
     * Pending, wait for start
     */
    PENDING,

    /**
     * Starting
     */
    STARTING,

    /**
     * Started
     */
    STARTED,

    /**
     * Stopping
     */
    STOPPING,

    /**
     * Stopped
     */
    STOPPED,

    /**
     * Failed
     */
    FAILED
}

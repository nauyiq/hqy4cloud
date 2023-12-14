package com.hqy.cloud.util.concurrent.async.tool.worker;

/**
 * 结果状态
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 11:29
 */
public enum ResultState {

    /**
     * 成功
     */
    SUCCESS,

    /**
     * 超时
     */
    TIMEOUT,

    /**
     * 异常
     */
    EXCEPTION,

    /**
     * 默认
     */
    DEFAULT
}

package com.hqy.cloud.auth.flow;

/**
 * 限流模式
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/16 17:12
 */
public enum LimitMode {

    /**
     * 线程数
     */
    THREAD_COUNT,

    /**
     * qps
     */
    QPS


}

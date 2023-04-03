package com.hqy.foundation.limit;

/**
 * 限流算法枚举
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 10:52
 */
public enum LimitAlgorithm {

    /**
     * 计数器
     */
    COUNTER,

    /**
     * 固定窗口
     */
    FIXED_WINDOW,

    /**
     * 滑动窗口
     */
    SLIDING_WINDOW,

    /**
     * 漏桶
     */
    LEAKY_BUCKET,

    /**
     * 令牌桶
     */
    TOKEN_BUCKET





    }

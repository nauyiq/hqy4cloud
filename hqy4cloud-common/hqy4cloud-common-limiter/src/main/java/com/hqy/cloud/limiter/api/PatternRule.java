package com.hqy.cloud.limiter.api;


import com.hqy.cloud.limiter.core.LimitAlgorithm;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 14:59
 */
public interface PatternRule {

    /**
     * 获取限流策略的url pattern
     * @return 例如/oauth/**
     */
    String pattern();

    /**
     * 获取限流策略
     * @return LimitStrategy
     */
    LimitAlgorithm strategy();


}

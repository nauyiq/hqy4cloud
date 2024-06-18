package com.hqy.cloud.limiter.core;


import com.hqy.cloud.limiter.api.PatternRule;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 14:44
 */
public abstract class RoutingRule implements PatternRule {

    /**
     * 服务名或资源名或路由id
     */
    private final String name;

    /**
     * 匹配的pattern
     */
    private final String pattern;

    /**
     * url匹配的策略 默认1 即前缀匹配
     */
    private final int urlMatchStrategy;

    /**
     * 限流策略
     */
    private final LimitAlgorithm algorithm;


    public RoutingRule(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
        this.urlMatchStrategy = 1;
        this.algorithm = LimitAlgorithm.SLIDING_WINDOW;
    }

    public RoutingRule(String name, String pattern, LimitAlgorithm algorithm, int urlMatchStrategy) {
        this.name = name;
        this.pattern = pattern;
        this.algorithm = algorithm;
        this.urlMatchStrategy = urlMatchStrategy;
    }

    @Override
    public LimitAlgorithm strategy() {
        return algorithm;
    }

    @Override
    public String pattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    public int getUrlMatchStrategy() {
        return urlMatchStrategy;
    }
}

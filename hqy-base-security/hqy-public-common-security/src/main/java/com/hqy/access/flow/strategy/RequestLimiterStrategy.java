package com.hqy.access.flow.strategy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 16:01
 */
@FunctionalInterface
public interface RequestLimiterStrategy {

    /**
     * 无状态 判断当前请求是否超限
     * @return true 超限
     */
    boolean isLimit();


}

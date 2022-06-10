package com.hqy.access.flow.strategy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/9 10:51
 */
public abstract class AbstractBucketLimiterTemplate implements RequestLimiterStrategy {


    @Override
    public final boolean isLimit() {



        return false;
    }
}

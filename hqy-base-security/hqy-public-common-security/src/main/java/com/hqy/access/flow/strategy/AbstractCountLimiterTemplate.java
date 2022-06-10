package com.hqy.access.flow.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 计数器类型限流器模板
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/8 14:45
 */
@Slf4j
public abstract class AbstractCountLimiterTemplate implements RequestLimiterStrategy {

    /***
     * 允许通过的数目
     */
    protected final long count;
    
    /**
     * 时间窗口
     */
    protected final long secondWindows;

    /**
     * 获取当前t时刻系统的访问量
     * @return
     */
    protected abstract long currentCount();




    public AbstractCountLimiterTemplate(long count) {
        this.count = count;
        this.secondWindows = 0L;
    }

    public AbstractCountLimiterTemplate(long count, long secondWindows) {
        this.count = count;
        this.secondWindows = secondWindows;
    }

    @Override
    public final boolean isLimit() {
        //获取当前t时刻系统的访问量
        long currentCount = currentCount();
        return currentCount > count;
    }

    public long getCount() {
        return count;
    }

    public long getSecondWindows() {
        return secondWindows;
    }
}

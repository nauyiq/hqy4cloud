package com.hqy.access.flow.strategy.window;

import com.hqy.access.flow.strategy.AbstractCountLimiterTemplate;

/**
 * 抽象的窗口算法限流器
 *
 * 1. 固定窗口算法
 * 在固定的时间窗口内，可以允许固定数量的请求进入。超过数量就拒绝或者排队，等下一个时间段进入。
 * 缺点：在一个窗口临界点的前后时间，比如时间窗口是1分钟，在59秒和1分01秒同时突发大量请求，极端情况下可能会带来 2 倍的流量，系统可能承受不了这么大的突发性流量。
 *
 * 2.滑动窗口算法
 * 可有有效解决固定时间窗口算法带来的问题 缺点就是实现比较复杂
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/7 10:24
 */
public abstract class AbstractTimeWindowsLimiter extends AbstractCountLimiterTemplate {

    public AbstractTimeWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
    }

}

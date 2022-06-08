package com.hqy.access.flow.strategy.counter;

import com.hqy.access.flow.strategy.AbstractCountLimiterTemplate;

/**
 * 线程级别的计数器 与时间单位无关 简单粗暴，单机在 Java 中可用 Atomic 等原子类、分布式就 Redis incr。<br/>
 * 无法有效解决突发流量  假设我们允许的阈值是1万，此时计数器的值为0， 当1万个请求在前1秒内一股脑儿的都涌进来，这突发的流量可是顶不住的
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 17:34
 */
public abstract class AbstractCounterLimiter extends AbstractCountLimiterTemplate {


    public AbstractCounterLimiter(long count) {
        super(count);
    }

    /**
     * 计数器自增
     * @return 自增后的数目
     */
    public abstract void increment();

    /**
     * 计数器减一
     * @return 自减后的数目
     */
    public abstract void decrement();

}

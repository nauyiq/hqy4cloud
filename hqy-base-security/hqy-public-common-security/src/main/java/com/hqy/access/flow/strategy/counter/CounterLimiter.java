package com.hqy.access.flow.strategy.counter;

import java.util.concurrent.atomic.LongAdder;

/**
 * 基于LongAdder 计时器算法的限流器 - 基本不使用
 * 在写多的场景下 其实推荐使用悲观锁。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/6 16:25
 */
public class CounterLimiter extends AbstractCounterLimiter {

    private static final LongAdder COUNTER = new LongAdder();

    public CounterLimiter(long count) {
        super(count);
    }


    @Override
    protected long currentCount() {
        return COUNTER.longValue();
    }

    @Override
    public void increment() {
        COUNTER.increment();
    }

    @Override
    public void decrement() {
        COUNTER.decrement();
    }



}

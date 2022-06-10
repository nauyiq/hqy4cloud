package com.hqy.access.flow.strategy.window;

import com.alibaba.csp.sentinel.slots.statistic.base.LongAdder;
import com.alibaba.csp.sentinel.slots.statistic.base.UnaryLeapArray;
import com.hqy.base.common.base.lang.BaseMathConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/7 16:25
 */
@Slf4j
public class SlidingTimeWindowsLimiter extends AbstractTimeWindowsLimiter {

    private final UnaryLeapArray data;

    /**
     * 滑动窗口 窗口数目
     */
    private int sampleCount = 2;

    /**
     * 总窗口大小
     */
    private int intervalInMs = 1000;


    public SlidingTimeWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
        if (secondWindows * intervalInMs != intervalInMs) {
            intervalInMs = (int) secondWindows;
        }
        data = new UnaryLeapArray(sampleCount, intervalInMs);
    }

    public SlidingTimeWindowsLimiter(long count, long secondWindows, int sampleCount, int intervalInMs) {
        super(count, secondWindows);
        this.sampleCount = sampleCount;
        this.intervalInMs = intervalInMs;
        data = new UnaryLeapArray(sampleCount, intervalInMs);
    }

    @Override
    protected long currentCount() {
        LongAdder adder = data.currentWindow().value();
        adder.increment();
        return adder.sum();
    }


    public static void main(String[] args) throws InterruptedException {
        SlidingTimeWindowsLimiter limiter = new SlidingTimeWindowsLimiter(100L, 1);

        for (int i = 0; i < 1000; i++) {

            boolean limit = limiter.isLimit();
            if (limit) {
//                TimeUnit.MILLISECONDS.sleep(100L);
                TimeUnit.MILLISECONDS.sleep(250L);
            }
            System.out.println("current thread limit " + limit);
        }
    }
}

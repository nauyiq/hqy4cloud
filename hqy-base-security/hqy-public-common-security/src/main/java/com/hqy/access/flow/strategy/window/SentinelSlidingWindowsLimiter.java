package com.hqy.access.flow.strategy.window;

import com.alibaba.csp.sentinel.slots.statistic.base.LongAdder;
import com.alibaba.csp.sentinel.slots.statistic.base.UnaryLeapArray;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/7 16:25
 */
public class SentinelSlidingWindowsLimiter extends AbstractWindowLimiter {

    private final UnaryLeapArray data;

    public SentinelSlidingWindowsLimiter(long count, long secondWindows) {
        super(count, secondWindows);
        data = new UnaryLeapArray((int) count, (int)secondWindows * 1000);
    }

    @Override
    protected long currentCount() {
        LongAdder adder = data.currentWindow().value();
        adder.increment();
        return adder.sum();
    }


    public static void main(String[] args) throws InterruptedException {
        SentinelSlidingWindowsLimiter limiter = new SentinelSlidingWindowsLimiter(10L, 1);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {

                    boolean limit = limiter.isLimit();
                    if (limit) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("current thread limit " + limit);
                }
            }
        });
        thread.start();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {

                    boolean limit = limiter.isLimit();
                    if (limit) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("current thread limit " + limit);
                }
            }
        });
        thread1.start();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {

                    boolean limit = limiter.isLimit();
                    if (limit) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("current thread limit " + limit);
                }
            }
        });
        thread2.start();

        for (int i = 0; i < 10000; i++) {

            boolean limit = limiter.isLimit();
            if (limit) {
                TimeUnit.MILLISECONDS.sleep(100L);
            }
            System.out.println("current thread limit " + limit);
        }
    }
}

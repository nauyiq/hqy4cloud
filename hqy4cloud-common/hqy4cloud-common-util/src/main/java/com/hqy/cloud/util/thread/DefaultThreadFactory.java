package com.hqy.cloud.util.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统一命名规范 业务线程池 取个名字，
 * 免得多线程调试时看不到哪个是我们的业务线程！
 * @author qy
 * @date 2021-08-16 15:14
 */
public class DefaultThreadFactory implements ThreadFactory {

    private final AtomicInteger count;

    private final String threadNamePrefix;

    public DefaultThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        this.count = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(String.format("pool-%s-%s", this.threadNamePrefix, count.getAndIncrement()));
        return t;
    }
}

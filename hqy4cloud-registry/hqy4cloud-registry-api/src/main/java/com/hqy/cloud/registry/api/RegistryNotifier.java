package com.hqy.cloud.registry.api;

import com.hqy.cloud.registry.common.Constants;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RegistryNotifier.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public abstract class RegistryNotifier<T> {
    private static final Logger log = LoggerFactory.getLogger(RegistryNotifier.class);

    private volatile long lastExecuteTime;
    private volatile long lastEventTime;
    private final long delayTime;
    private final AtomicBoolean shouldDelay = new AtomicBoolean(false);
    private final AtomicInteger executeTime = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler;
    private T rawAddresses;

    public RegistryNotifier(long delayTime) {
        this(delayTime, null);
    }

    public RegistryNotifier(long delayTime, ScheduledExecutorService scheduler) {
        this.delayTime = delayTime;
        this.scheduler = Objects.requireNonNullElseGet(scheduler, () -> Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("hqy4cloud-registry-notification")));
    }

    public synchronized void notify(T rawAddresses) {
        this.rawAddresses = rawAddresses;
        long notifyTime = System.currentTimeMillis();
        this.lastEventTime = notifyTime;

        long delta = (System.currentTimeMillis() - lastExecuteTime) - delayTime;

        // more than 10 calls && next execute time is in the future
        boolean delay = shouldDelay.get() && delta < 0;
        if (delay) {
            scheduler.schedule(new NotificationTask<>(this, notifyTime), -delta, TimeUnit.MILLISECONDS);
        } else {
            // check if more than 10 calls
            if (!shouldDelay.get() && executeTime.incrementAndGet() > Constants.DEFAULT_DELAY_EXECUTE_TIMES) {
                shouldDelay.set(true);
            }
            scheduler.submit(new NotificationTask<>(this, notifyTime));
        }
    }

    public long getDelayTime() {
        return delayTime;
    }

    /**
     * notification of instance addresses (aka providers).
     *
     * @param rawAddresses data.
     */
    protected abstract void doNotify(T rawAddresses);

    public record NotificationTask<T>(RegistryNotifier<T> listener,
                                      long time) implements Runnable {

        @Override
        public void run() {
            try {
                if (this.time == listener.lastEventTime) {
                    listener.doNotify(listener.rawAddresses);
                    listener.lastExecuteTime = System.currentTimeMillis();
                    synchronized (listener) {
                        if (this.time == listener.lastEventTime) {
                            listener.rawAddresses = null;
                        }
                    }
                }
            } catch (Throwable t) {
                log.error("Error occurred when notify directory. ", t);
            }
        }
    }


}

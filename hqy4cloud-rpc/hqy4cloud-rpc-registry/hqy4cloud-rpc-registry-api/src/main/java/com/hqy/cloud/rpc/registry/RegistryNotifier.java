package com.hqy.cloud.rpc.registry;

import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hqy.cloud.rpc.registry.Constants.DEFAULT_DELAY_EXECUTE_TIMES;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/5 16:48
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

    public RegistryNotifier(RPCModel rpcModel, long delayTime) {
        this(rpcModel, delayTime, null);
    }


    public RegistryNotifier(RPCModel rpcModel, long delayTime, ScheduledExecutorService scheduler) {
        this.delayTime = delayTime;
        if (scheduler == null) {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("thrift-registry-notification"));
        } else {
            this.scheduler = scheduler;
        }
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
            if (!shouldDelay.get() && executeTime.incrementAndGet() > DEFAULT_DELAY_EXECUTE_TIMES) {
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

    public static class NotificationTask<T> implements Runnable {
        private final RegistryNotifier<T> listener;
        private final long time;

        public NotificationTask(RegistryNotifier<T> listener, long time) {
            this.listener = listener;
            this.time = time;
        }

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

package com.hqy.rpc.registry.retry;

import com.hqy.foundation.timer.Timeout;
import com.hqy.foundation.timer.Timer;
import com.hqy.foundation.timer.TimerTask;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.support.FailBackRegistry;
import com.hqy.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hqy.rpc.registry.Constants.*;

/**
 * base retry task
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/24 17:31
 */
public abstract class AbstractRetryTask implements TimerTask {

    protected final Logger log = LoggerFactory.getLogger(AbstractRetryTask.class);

    /**
     * url for retry task
     */
    protected final Metadata metadata;

    /**
     * registry for this task
     */
    protected final FailBackRegistry registry;

    /**
     * retry period
     */
    private final long retryPeriod;

    /**
     * define the most retry times
     */
    private final int retryTimes;

    /**
     * task name for this task
     */
    private final String taskName;

    /**
     * times of retry.
     * retry task is execute in single thread so that the times is not need volatile.
     */
    private int times = 1;

    private volatile boolean cancel;

    AbstractRetryTask(Metadata metadata, FailBackRegistry registry, String taskName) {
        AssertUtil.isFalse(Objects.isNull(metadata) || StringUtils.isBlank(taskName), "Initial retry task failure. url or taskName error.");

        this.metadata = metadata;
        this.registry = registry;
        this.taskName = taskName;
        cancel = false;
        this.retryPeriod = metadata.getParameter(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);
        this.retryTimes = metadata.getParameter(REGISTRY_RETRY_TIMES_KEY, DEFAULT_REGISTRY_RETRY_TIMES);
    }


    public void cancel() {
        cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    protected void rePut(Timeout timeout, long tick) {
        AssertUtil.notNull(timeout, "rePut error, timeout is null.");
        Timer timer = timeout.timer();
        if (timer.isStop() || timeout.isCancelled() || isCancel()) {
            return;
        }
        times++;
        timer.newTimeout(timeout.task(), tick, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (timeout.isCancelled() || timeout.timer().isStop() || isCancel()) {
            // other thread cancel this timeout or stop the timer.
            return;
        }
        if (times > retryTimes) {
            // reach the most times of retry.
            log.warn("Final failed to execute task {}, url {}, retry {} times.",  taskName, metadata, retryTimes);
            return;
        }

        try {
            doRetry(metadata, registry, timeout);
        } catch (Throwable t) {
            log.warn("Failed to execute task {}, url: {},  waiting for again, cause: {}", taskName, metadata, t.getMessage(), t);
            // rePut this task when catch exception.
            rePut(timeout, retryPeriod);
        }
    }

    /**
     * do retry.
     * @param metadata       url information
     * @param registry  registry
     * @param timeout   timeout handler
     */
    protected abstract void doRetry(Metadata metadata, FailBackRegistry registry, Timeout timeout);
}

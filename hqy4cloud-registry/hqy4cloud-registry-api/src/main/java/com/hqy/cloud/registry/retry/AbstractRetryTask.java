package com.hqy.cloud.registry.retry;

import com.hqy.cloud.registry.api.FailedBackRegistry;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.timer.Timeout;
import com.hqy.foundation.timer.Timer;
import com.hqy.foundation.timer.TimerTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.registry.common.Constants.*;

/**
 * base retry task
 * @author qiyuan.hong
 * @version 1.0
 */
public abstract class AbstractRetryTask implements TimerTask {
    protected final Logger log = LoggerFactory.getLogger(AbstractRetryTask.class);

    /**
     * model for retry task
     */
    protected final ApplicationModel model;

    /**
     * registry for this task
     */
    protected final FailedBackRegistry registry;

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

    AbstractRetryTask(ApplicationModel model, FailedBackRegistry registry, String taskName) {
        AssertUtil.isFalse(Objects.isNull(model) || StringUtils.isBlank(taskName), "Initial retry task failure. url or taskName error.");
        this.model = model;
        this.registry = registry;
        this.taskName = taskName;
        cancel = false;
        this.retryPeriod = model.getParameter(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);
        this.retryTimes = model.getParameter(REGISTRY_RETRY_TIMES_KEY, DEFAULT_REGISTRY_RETRY_TIMES);
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
            log.warn("Final failed to execute task {}, url {}, retry {} times.",  taskName, model, retryTimes);
            return;
        }

        try {
            doRetry(model, registry, timeout);
        } catch (Throwable t) {
            log.warn("Failed to execute task {}, url: {},  waiting for again, cause: {}", taskName, model, t.getMessage(), t);
            // rePut this task when catch exception.
            rePut(timeout, retryPeriod);
        }
    }

    /**
     * do retry.
     * @param model     applicationModel
     * @param registry  registry
     * @param timeout   timeout handler
     */
    protected abstract void doRetry(ApplicationModel model, FailedBackRegistry registry, Timeout timeout);
}

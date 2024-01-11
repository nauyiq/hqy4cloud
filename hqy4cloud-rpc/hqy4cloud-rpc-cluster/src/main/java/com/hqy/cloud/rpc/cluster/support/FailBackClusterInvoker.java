package com.hqy.cloud.rpc.cluster.support;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.foundation.timer.HashedWheelTimer;
import com.hqy.foundation.timer.Timeout;
import com.hqy.foundation.timer.Timer;
import com.hqy.foundation.timer.TimerTask;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import com.hqy.cloud.rpc.cluster.loadbalance.LoadBalance;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * When fails, record failure requests and schedule for retry on a regular interval.
 * Especially useful for services of notification.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13
 */
public class FailBackClusterInvoker<T> extends AbstractClusterInvoker<T> {
    private static final Logger log = LoggerFactory.getLogger(FailBackClusterInvoker.class);

    private static final long RETRY_FAILED_PERIOD = 5;

    /**
     * Number of retries obtained from the configuration, don't contain the first invoke.
     */
    private final int retries;

    private final int failBackTasks;

    private volatile Timer failTimer;

    public FailBackClusterInvoker(Directory<T> directory, Map<String, Object> attachments) {
        super(directory, attachments);
        int retriesParams = getModel().getParameter(RPC_CLUSTER_RETRIES_TIMES, DEFAULT_FAIL_BACK_TIMES);
        if (retriesParams < 0) {
            retriesParams = DEFAULT_FAIL_BACK_TIMES;
        }
        retries = retriesParams;

        int failBackTasksConfig = getModel().getParameter(RPC_CLUSTER_FAIL_BACK_TASKS_KEY, DEFAULT_FAIL_BACK_TASKS);
        if (failBackTasksConfig <= 0) {
            failBackTasksConfig = DEFAULT_FAIL_BACK_TASKS;
        }
        failBackTasks = failBackTasksConfig;
    }


    @Override
    public void destroy() {
        super.destroy();
        if (failTimer != null) {
            failTimer.stop();
        }
    }

    @Override
    protected Object doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException {
        Invoker<T> invoker = null;
        RpcModel rpcModel = getModel();
        try {
            checkInvokers(invokers, invocation);
            invoker = select(loadBalance, invocation, invokers, null);
            return invoker.invoke(invocation);
        } catch (Throwable t) {
            log.error("FailBack to invoke method " + invocation.getMethodName() + ", wait for retry in background. Ignored exception: "
                    + t.getMessage() + ", ", t);
            if (retries > 0) {
                addFailed(invocation, invokers, invoker, loadBalance, rpcModel);
            }
        }
        return null;
    }

    private void addFailed(Invocation invocation, List<Invoker<T>> invokers, Invoker<T> invoker, LoadBalance loadBalance, RpcModel rpcModel) {
        if (failTimer == null) {
            synchronized (this) {
                if (failTimer == null) {
                    failTimer = new HashedWheelTimer(new NamedThreadFactory("failBack-cluster-timer", true),
                            1, TimeUnit.SECONDS, 32, failBackTasks);
                }
            }
        }
        RetryTimerTask retryTimerTask = new RetryTimerTask(loadBalance, invocation, invokers,  retries, RETRY_FAILED_PERIOD, invoker, rpcModel);
        try {
            failTimer.newTimeout(retryTimerTask, RETRY_FAILED_PERIOD, TimeUnit.SECONDS);
        } catch (Throwable e) {
            log.error("FailBack background works error, invocation->" + invocation + ", exception: " + e.getMessage());
        }
    }

    private class RetryTimerTask implements TimerTask {
        private final Invocation invocation;
        private final LoadBalance loadbalance;
        private final List<Invoker<T>> invokers;
        private final long tick;
        private Invoker<T> lastInvoker;
        private RpcModel rpcModel;
        /**
         * Number of retries obtained from the configuration, don't contain the first invoke.
         */
        private final int retries;

        /**
         * Number of retried.
         */
        private int retriedTimes = 0;

        public RetryTimerTask(LoadBalance loadbalance, Invocation invocation, List<Invoker<T>> invokers, int retries, long tick, Invoker<T> lastInvoker, RpcModel rpcModel) {
            this.invocation = invocation;
            this.loadbalance = loadbalance;
            this.invokers = invokers;
            this.tick = tick;
            this.lastInvoker = lastInvoker;
            this.retries = retries;
            this.rpcModel = rpcModel;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            try {
                log.info("Attempt to retry to invoke method " + invocation.getMethodName() +
                        ". The total will retry " + retries + " times, the current is the " + retriedTimes + " retry");
                Invoker<T> retryInvoker = select(loadbalance, invocation, invokers, Collections.singletonList(lastInvoker));
                lastInvoker = retryInvoker;
                retryInvoker.invoke(invocation);
            } catch (Throwable t) {
                log.error("Failed retry to invoke method " + invocation.getMethodName() + ", waiting again.", t);
                if ((++retriedTimes) >= retries) {
                    log.error("Failed retry times exceed threshold (" + retries + "), We have to abandon, invocation->" + invocation);
                } else {
                    rePut(timeout);
                }
            }
        }

        private void rePut(Timeout timeout) {
            if (timeout == null) {
                return;
            }

            Timer timer = timeout.timer();
            if (timer.isStop() || timeout.isCancelled()) {
                return;
            }

            timer.newTimeout(timeout.task(), tick, TimeUnit.SECONDS);
        }

    }





}

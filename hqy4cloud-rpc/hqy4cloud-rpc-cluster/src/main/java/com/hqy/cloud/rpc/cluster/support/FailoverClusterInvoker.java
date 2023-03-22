package com.hqy.cloud.rpc.cluster.support;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import com.hqy.cloud.rpc.cluster.loadbalance.LoadBalance;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * When invoke fails, log the initial error and retry other invokers (retry n times, which means at most n different invokers will be invoked)
 * Note that retry causes latency.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 13:58
 */
public class FailoverClusterInvoker<T> extends AbstractClusterInvoker<T> {

    private final static Logger log = LoggerFactory.getLogger(FailoverClusterInvoker.class);

    public FailoverClusterInvoker(Directory<T> directory, String hashFactor) {
        super(directory, hashFactor);
    }

    @Override
    protected Object doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException {
        List<Invoker<T>> copyInvokers = invokers;
        checkInvokers(copyInvokers, invocation);
        String methodName = invocation.getMethodName();
        int len = calculateInvokeTimes();
        // retry loop.
        // last exception.
        RpcException le = null;
        // invoked invokers.
        List<Invoker<T>> invoked = new ArrayList<>(copyInvokers.size());
        Set<String> providers = new HashSet<>(len);
        for (int i = 0; i < len; i++) {
            //Reselect before retry to avoid a change of candidate `invokers`.
            //NOTE: if `invokers` changed, then `invoked` also lose accuracy.
            if (i > 0) {
                checkWhetherDestroyed();
                copyInvokers = list(getModel(), getHashFactor());
                // check again
                checkInvokers(copyInvokers, invocation);
            }
            Invoker<T> invoker = select(loadBalance, invocation, copyInvokers, invoked);
            invoked.add(invoker);
            boolean success = false;
            try {
                Object result = invoker.invoke(invocation);
                if (le != null && log.isWarnEnabled()) {
                    log.warn("Although retry the method " + methodName
                            + " in the service " + getInterface().getName()
                            + " was successful by the provider " + invoker.getModel().getHost()
                            + ", but there have been failed providers " + providers
                            + " (" + providers.size() + "/" + copyInvokers.size()
                            + ") from the registry " + directory.getConsumerModel().getRegistryAddress()
                            + " on the consumer " + IpUtil.getHostAddress()
                            + le.getMessage(), le);
                }
                success = true;
                return result;
            } catch (RpcException e) {
                // biz exception.
                if (e.isBiz()) {
                    throw e;
                }
                le = e;
            } catch (Throwable e) {
                le = new RpcException(e.getMessage(), e);
            } finally {
                if (!success) {
                    providers.add(invoker.getModel().getHost());
                }
            }
        }
        throw new RpcException(le == null ? 0 : le.getCode() , "Failed to invoke the method "
                + methodName + " in the service " + getInterface().getName()
                + ". Tried " + len + " times of the providers " + providers
                + " (" + providers.size() + "/" + copyInvokers.size()
                + ") from the registry " + directory.getConsumerModel().getRegistryAddress()
                + " on the consumer " + IpUtil.getHostAddress());
    }

    private int calculateInvokeTimes() {
        int len = getModel().getParameter(CommonConstants.RPC_CLUSTER_RETRIES_TIMES, CommonConstants.DEFAULT_RETRIES) + 1;
        if (len <= 0) {
            len = 1;
        }
        return len;
    }

}

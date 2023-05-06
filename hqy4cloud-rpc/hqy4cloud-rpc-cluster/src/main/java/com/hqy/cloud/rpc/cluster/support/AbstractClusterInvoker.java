package com.hqy.cloud.rpc.cluster.support;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.Result;
import com.hqy.cloud.rpc.cluster.ClusterInvoker;
import com.hqy.cloud.rpc.cluster.directory.Directory;
import com.hqy.cloud.rpc.cluster.loadbalance.LoadBalance;
import com.hqy.cloud.rpc.cluster.loadbalance.RandomLoadBalance;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * AbstractClusterInvoker. {@link ClusterInvoker}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 10:06
 */
public abstract class AbstractClusterInvoker<T> implements ClusterInvoker<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractClusterInvoker.class);

    protected Directory<T> directory;

    protected boolean availableCheck;

    private volatile boolean enableConnectivityValidation = true;

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private final String hashFactor;

    private volatile Invoker<T> stickyInvoker = null;

    private int reselectCount = 10;

    public AbstractClusterInvoker(Directory<T> directory, String hashFactor) {
        this(directory, directory.getConsumerModel(), hashFactor);
    }

    public AbstractClusterInvoker(Directory<T> directory, RPCModel rpcModel, String hashFactor) {
        AssertUtil.notNull(directory, "Directory should not be null.");
        this.directory = directory;
        this.hashFactor = hashFactor;
        this.availableCheck = rpcModel.getParameter(RPC_CLUSTER_AVAILABLE_CHECK, DEFAULT_RPC_CLUSTER_AVAILABLE_CHECK);
    }

    @Override
    public Class<T> getInterface() {
        return getDirectory().getInterface();
    }

    @Override
    public RPCModel getModel() {
        return getDirectory().getConsumerModel();
    }

    @Override
    public RPCModel getConsumerModel() {
        return getDirectory().getConsumerModel();
    }

    public String getHashFactor() {
        return hashFactor;
    }

    @Override
    public boolean isAvailable() {
        Invoker<T> invoker = stickyInvoker;
        if (invoker != null) {
            return invoker.isAvailable();
        }
        return getDirectory().isAvailable();
    }

    @Override
    public Directory<T> getDirectory() {
        return directory;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            getDirectory().destroy();
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed.get();
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        List<Invoker<T>> invokers = list(getModel(), hashFactor);
        LoadBalance loadBalance =  initLoadBalance(invokers, invocation);
        return doInvoke(invocation, invokers, loadBalance);
    }

    /**
     * Select a invoker using loadBalance policy.</br>
     * a) Firstly, select an invoker using loadBalance. If this invoker is in previously selected list, or,
     * if this invoker is unavailable, then continue step b (reselect), otherwise return the first selected invoker</br>
     * <p>
     * b) Reselection, the validation rule for reselection: selected > available. This rule guarantees that
     * the selected invoker has the minimum chance to be one in the previously selected list, and also
     * guarantees this invoker is available.
     *
     * @param loadBalance load balance policy
     * @param invocation  invocation
     * @param invokers    invoker candidates
     * @param selected    exclude selected invokers or not
     * @return the invoker which will final to do invoke.
     * @throws RpcException exception
     */
    protected Invoker<T> select(LoadBalance loadBalance, Invocation invocation,
                                List<Invoker<T>> invokers, List<Invoker<T>> selected) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        boolean sticky = invokers.get(0).getModel().getParameter(RPC_CLUSTER_STICKY_KEY, DEFAULT_RPC_CLUSTER_STICKY);

        if (stickyInvoker != null && !invokers.contains(stickyInvoker)) {
            return stickyInvoker;
        }

        if (sticky && stickyInvoker != null && (selected == null || !selected.contains(stickyInvoker))) {
            if (availableCheck && stickyInvoker.isAvailable()) {
                return stickyInvoker;
            }
        }

        Invoker<T> invoker = doSelect(loadBalance, invocation, invokers, selected);

        if (sticky) {
            stickyInvoker = invoker;
        }

        return invoker;

    }

    private Invoker<T> doSelect(LoadBalance loadBalance, Invocation invocation, List<Invoker<T>> invokers, List<Invoker<T>> selected) {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }

        if (invokers.size() == 1) {
            Invoker<T> tInvoker = invokers.get(0);
            checkShouldInvalidateInvoker(tInvoker);
            return tInvoker;
        }

        Invoker<T> invoker = loadBalance.select(invokers, getModel()  );

        //If the `invoker` is in the  `selected` or invoker is unavailable && availablecheck is true, reselect.
        boolean isSelected = selected != null && selected.contains(invoker);
        boolean isUnavailable = availableCheck && !invoker.isAvailable() && getModel() != null;

        if (isUnavailable) {
            invalidateInvoker(invoker);
        }

        if (isSelected || isUnavailable) {
            try {
                Invoker<T> rInvoker = reselect(loadBalance, invocation, invokers, selected, availableCheck);
                if (rInvoker != null) {
                    invoker = rInvoker;
                } else {
                    //Check the index of current selected invoker, if it's not the last one, choose the one at index+1.
                    int index = invokers.indexOf(invoker);
                    try {
                        //Avoid collision
                        invoker = invokers.get((index + 1) % invokers.size());
                    } catch (Exception e) {
                        log.warn(e.getMessage() + " may because invokers list dynamic change, ignore.", e);
                    }
                }
            } catch (Throwable t) {
                log.error("cluster reselect fail reason is :" + t.getMessage() + " if can not solve, you can set cluster. availableCheck = false in context", t);
            }
        }

        return invoker;

    }

    /**
     * Reselect, use invokers not in `selected` first, if all invokers are in `selected`,
     * just pick an available one using loadbalance policy.
     *
     * @param loadBalance    load balance policy
     * @param invocation     invocation
     * @param invokers       invoker candidates
     * @param selected       exclude selected invokers or not
     * @param availableCheck check invoker available if true
     * @return the reselect result to do invoke
     * @throws RpcException exception
     */
    private Invoker<T> reselect(LoadBalance loadBalance, Invocation invocation, List<Invoker<T>> invokers, List<Invoker<T>> selected, boolean availableCheck) {
        // Allocating one in advance, this list is certain to be used.
        List<Invoker<T>> reselectInvokers = new ArrayList<>(Math.min(invokers.size(), reselectCount));

        // 1. Try picking some invokers not in `selected`.
        //    1.1. If all selectable invokers' size is smaller than reselectCount, just add all
        //    1.2. If all selectable invokers' size is greater than reselectCount, randomly select reselectCount.
        //            The result size of invokers might smaller than reselectCount due to disAvailable or de-duplication (might be zero).
        //            This means there is probable that reselectInvokers is empty however all invoker list may contain available invokers.
        //            Use reselectCount can reduce retry times if invokers' size is huge, which may lead to long time hang up.
        if (reselectCount >= invokers.size()) {
            for (Invoker<T> invoker : invokers) {
                // check if available
                if (availableCheck && !invoker.isAvailable()) {
                    // add to invalidate invoker
                    invalidateInvoker(invoker);
                    continue;
                }

                if (selected == null || !selected.contains(invoker)) {
                    reselectInvokers.add(invoker);
                }
            }
        } else {
            for (int i = 0; i < reselectCount; i++) {
                // select one randomly
                Invoker<T> invoker = invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
                // check if available
                if (availableCheck && !invoker.isAvailable()) {
                    // add to invalidate invoker
                    invalidateInvoker(invoker);
                    continue;
                }
                // de-duplication
                if (selected == null || !selected.contains(invoker) || !reselectInvokers.contains(invoker)) {
                    reselectInvokers.add(invoker);
                }
            }
        }

        // 2. Use loadBalance to select one (all the reselectInvokers are available)
        if (!reselectInvokers.isEmpty()) {
            return loadBalance.select(reselectInvokers, getModel());
        }

        // 3. reselectInvokers is empty. Unable to find at least one available invoker.
        //    Re-check all the selected invokers. If some in the selected list are available, add to reselectInvokers.
        if (selected != null) {
            for (Invoker<T> invoker : selected) {
                // available first
                if ((invoker.isAvailable())
                        && !reselectInvokers.contains(invoker)) {
                    reselectInvokers.add(invoker);
                }
            }
        }

        // 4. If reselectInvokers is not empty after re-check.
        //    Pick an available invoker using loadBalance policy
        if (!reselectInvokers.isEmpty()) {
            return loadBalance.select(reselectInvokers, getModel());
        }

        // 5. No invoker match, return null.
        return null;
    }

    private void checkShouldInvalidateInvoker(Invoker<T> invoker) {
        if (availableCheck && !invoker.isAvailable()) {
            invalidateInvoker(invoker);
        }
    }

    protected void checkWhetherDestroyed() {
        if (destroyed.get()) {
            throw new RpcException("Rpc cluster invoker for " + getInterface() + " on consumer " + IpUtil.getHostAddress()
                    + " is now destroyed! Can not invoke any more.");
        }
    }

    private void invalidateInvoker(Invoker<T> invoker) {
        if (enableConnectivityValidation) {
            if (getDirectory() != null) {
                getDirectory().addInvalidateInvoker(invoker);
            }
        }
    }

    public void setDirectory(Directory<T> directory) {
        this.directory = directory;
    }

    public void setAvailableCheck(boolean availableCheck) {
        this.availableCheck = availableCheck;
    }

    public void setEnableConnectivityValidation(boolean enableConnectivityValidation) {
        this.enableConnectivityValidation = enableConnectivityValidation;
    }

    public void setReselectCount(int reselectCount) {
        this.reselectCount = reselectCount;
    }

    protected abstract Object doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException;

    protected void checkInvokers(List<Invoker<T>> invokers, Invocation invocation) {
        if (CollectionUtils.isEmpty(invokers)) {
            throw new RpcException(RpcException.NO_INVOKER_AVAILABLE_AFTER_FILTER, "Failed to invoke the method "
                    + invocation.getMethodName() + " in the service " + getInterface().getName()
                    + ". No provider available for the service " + getDirectory().getConsumerModel().getName()
                    + " from registry " + getDirectory().getConsumerModel().getRegistryAddress()
                    + " on the consumer " + IpUtil.getHostAddress()
                    + ". Please check if the providers have been started and registered.");
        }
    }

    /**
     * Init LoadBalance.
     * if invokers is not empty, init from the first invoke's url and invocation
     * if invokes is empty, init a default LoadBalance(RandomLoadBalance)
     * @param invokers    invokers
     * @param invocation  invocation.
     * @return            {@link LoadBalance}
     */
    protected LoadBalance initLoadBalance(List<Invoker<T>> invokers, Invocation invocation) {
        if (CollectionUtils.isNotEmpty(invokers)) {
            //TODO
        }
        return new RandomLoadBalance();
    }

    protected List<Invoker<T>> list(RPCModel rpcModel, String hashFactor) throws RpcException {
        if (StringUtils.isNotBlank(hashFactor) || !DEFAULT_HASH_FACTOR.equals(hashFactor)) {
            rpcModel.setHashFactor(hashFactor);
        }
        return getDirectory().list(rpcModel);
    }
}

package com.hqy.cloud.rpc.cluster.directory;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.router.RouterChain;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static com.hqy.cloud.rpc.CommonConstants.DEFAULT_RECONNECT_TASK_PERIOD;
import static com.hqy.cloud.rpc.CommonConstants.DEFAULT_RECONNECT_TASK_TRY_COUNT;

/**
 * Abstract implementation of Directory: Invoker list returned from this Directory's list method have been filtered by Routers <br>
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30
 */
public abstract class AbstractDirectory<T> implements Directory<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDirectory.class);

    protected final String providerServiceName;
    protected final ProjectInfoModel providerModel;
    private volatile boolean destroyed = false;
    protected volatile RpcModel rpcModel;
    protected RouterChain<T> routerChain;

    /**
     * Invokers initialized flag.
     */
    private volatile boolean invokersInitialized = false;

    /**
     * All invokers from registry
     */
    private volatile List<Invoker<T>> invokers = Collections.emptyList();

    /**
     * Valid Invoker. All invokers from registry exclude unavailable and disabled invokers.
     */
    private volatile List<Invoker<T>> validInvokers = Collections.emptyList();

    /**
     * Waiting to reconnect invokers.
     */
    protected volatile List<Invoker<T>> invokersToReconnect = new CopyOnWriteArrayList<>();

    private final Semaphore checkConnectivityPermit = new Semaphore(1);

    /**
     * Disabled Invokers. Will not be recovered in reconnect task, but be recovered if registry remove it.
     */
    protected final Set<Invoker<T>> disabledInvokers = new ConcurrentHashSet<>();

    private final ScheduledExecutorService connectivityExecutor;

    /**
     * The max count of invokers for each reconnect task select to try to reconnect.
     */
    private final int reconnectTaskTryCount;

    /**
     * The period of reconnect task if needed. (in ms)
     */
    private final int reconnectTaskPeriod;


    public AbstractDirectory(String providerServiceName, Class<T> serviceClass, RpcModel rpcModel) {
        this(providerServiceName, rpcModel, RouterChain.buildChain(serviceClass, rpcModel));
    }

    public AbstractDirectory(String providerServiceName, RpcModel rpcModel, RouterChain<T> routerChain) {
        AssertUtil.notNull(rpcModel, "Rpc model is null.");
        this.providerServiceName = providerServiceName;
        this.rpcModel = rpcModel;
        this.providerModel = ProjectInfoModel.of(providerServiceName, rpcModel.getModel().getNamespace(), rpcModel.getModel().getGroup());
        setRouterChain(routerChain);
        connectivityExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("rpc-connectivity-scheduler", true));
        reconnectTaskTryCount = DEFAULT_RECONNECT_TASK_TRY_COUNT;
        reconnectTaskPeriod = DEFAULT_RECONNECT_TASK_PERIOD;
    }

    public void setInvokers(List<Invoker<T>> invokers) {
        this.invokers = invokers;
        refreshInvokerInternal();
        this.invokersInitialized = true;
    }

    /**
     * Refresh invokers from total invokers
     * 1. all the invokers in need to reconnect list should be removed in the valid invokers list
     * 2. all the invokers in disabled invokers list should be removed in the valid invokers list
     * 3. all the invokers disappeared from total invokers should be removed in the need to reconnect list
     * 4. all the invokers disappeared from total invokers should be removed in the disabled invokers list
     */
    public void refreshInvoker() {
        if (invokersInitialized) {
            refreshInvokerInternal();
        }
    }

    private synchronized void refreshInvokerInternal() {
        List<Invoker<T>> copiedInvokers = new LinkedList<>(invokers);
        refreshInvokers(copiedInvokers, invokersToReconnect);
        refreshInvokers(copiedInvokers, disabledInvokers);
        validInvokers = copiedInvokers;
    }

    private void refreshInvokers(List<Invoker<T>> targetInvokers, Collection<Invoker<T>> invokersToRemove) {
        List<Invoker<T>> needToRemove = new LinkedList<>();
        for (Invoker<T> invoker : invokersToRemove) {
            if (targetInvokers.contains(invoker)) {
                targetInvokers.remove(invoker);
            } else {
                needToRemove.add(invoker);
            }
        }
        invokersToRemove.removeAll(needToRemove);
    }

    @Override
    public void addInvalidateInvoker(Invoker<T> invoker) {
        // 1. remove this invoker from validInvokers list, this invoker will not be listed in the next time
        if (removeValidInvoker(invoker)) {
            // 2. add this invoker to reconnect list
            invokersToReconnect.add(invoker);
            // 3. try start check connectivity task
            checkConnectivity();
        }
    }

    private void checkConnectivity() {
        // try to submit task, to ensure there is only one task at most for each directory
        if (checkConnectivityPermit.tryAcquire()) {
            // 1. pick invokers from invokersToReconnect
            // limit max reconnectTaskTryCount, prevent this task hang up all the connectivityExecutor for long time
            // ignore if is selected, invokersToTry's size is always smaller than reconnectTaskTryCount + 1
            // 2. try to check the invoker's status
            // 3. recover valid invoker
            // 4. submit new task if it has more to recover
            ScheduledFuture<?> connectivityCheckFuture = connectivityExecutor.schedule(() -> {
                try {
                    if (isDestroyed()) {
                        return;
                    }

                    List<Invoker<T>> needDeleteList = new ArrayList<>();
                    List<Invoker<T>> invokersToTry = new ArrayList<>();
                    // 1. pick invokers from invokersToReconnect
                    // limit max reconnectTaskTryCount, prevent this task hang up all the connectivityExecutor for long time

                    if (invokersToReconnect.size() < reconnectTaskTryCount) {
                        invokersToTry.addAll(invokersToReconnect);
                    } else {
                        for (int i = 0; i < reconnectTaskTryCount; i++) {
                            Invoker<T> tInvoker = invokersToReconnect.get(ThreadLocalRandom.current().nextInt(invokersToReconnect.size()));
                            if (!invokersToTry.contains(tInvoker)) {
                                // ignore if is selected, invokersToTry's size is always smaller than reconnectTaskTryCount + 1
                                invokersToTry.add(tInvoker);
                            }
                        }
                    }

                    // 2. try to check the invoker's status
                    for (Invoker<T> invoker : invokersToTry) {
                        if (invokers.contains(invoker)) {
                            if (invoker.isAvailable()) {
                                needDeleteList.add(invoker);
                            }
                        } else {
                            needDeleteList.add(invoker);
                        }
                    }

                    // 3. recover valid invoker
                    for (Invoker<T> tInvoker : needDeleteList) {
                        if (invokers.contains(tInvoker)) {
                            addValidInvoker(tInvoker);
                            log.info("Recover service address: " + tInvoker.getModel() + "  from invalid list.");
                        }
                        invokersToReconnect.remove(tInvoker);
                    }

                } finally {
                    checkConnectivityPermit.release();
                }

                // 4. submit new task if it has more to recover
                if (!invokersToReconnect.isEmpty()) {
                    checkConnectivity();
                }

            }, reconnectTaskPeriod, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void addDisabledInvoker(Invoker<T> invoker) {
        if (invokers.contains(invoker)) {
            disabledInvokers.add(invoker);
            removeValidInvoker(invoker);
            log.info("Disable service address: " + invoker.getModel() + ".");
        }
    }

    public void setRouterChain(RouterChain<T> routerChain) {
        this.routerChain = routerChain;
    }

    @Override
    public RouterChain<T> getRouterChain() {
        return routerChain;
    }

    @Override
    public String getProviderServiceName() {
        return providerServiceName;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }


    @Override
    public void destroy() {
        destroyed = true;
        destroyInvokers();
        invokersToReconnect.clear();
        disabledInvokers.clear();
    }

    protected void destroyInvokers() {
        // set empty instead of clearing to support concurrent access.
        invokers.clear();
        validInvokers.clear();
        this.invokersInitialized = false;
    }


    @Override
    public List<Invoker<T>> list(Invocation invocation) throws RpcException {
        if (destroyed) {
            throw new RpcException("Directory of type " + this.getClass().getSimpleName() +  " already destroyed for service " + getProviderServiceName() + " from registry.");
        }
        List<Invoker<T>> availableInvokers;
        // use clone to avoid being modified at doList().
        if (invokersInitialized) {
            availableInvokers = new ArrayList<>(validInvokers);
        } else {
            availableInvokers = new ArrayList<>(invokers);
        }

        List<Invoker<T>> routedResult = doList(availableInvokers, invocation);
        if (routedResult.isEmpty()) {
            log.warn("No provider available after connectivity filter for the service " + getProviderServiceName()
                    + " All validInvokers' size: " + validInvokers.size()
                    + " All routed invokers' size: " + routedResult.size()
                    + " All invokers' size: " + invokers.size()
                    + " from registry " + getModel().getRegistryInfo()
                    + " on the consumer " + getModel().getHost()
                    + ".");
        }

        return Collections.unmodifiableList(routedResult);
    }

    @Override
    public ProjectInfoModel getModel() {
        return getRPCModel().getModel();
    }

    @Override
    public ProjectInfoModel getProviderModel() {
        return this.providerModel;
    }

    public List<Invoker<T>> getInvokers() {
        return invokers;
    }

    public List<Invoker<T>> getValidInvokers() {
        return validInvokers;
    }

    private boolean addValidInvoker(Invoker<T> invoker) {
        synchronized (this.validInvokers) {
            return this.validInvokers.add(invoker);
        }
    }

    private boolean removeValidInvoker(Invoker<T> invoker) {
        synchronized (this.validInvokers) {
            return this.validInvokers.remove(invoker);
        }
    }

    /**
     * choose conditional invokers.
     * @param availableInvokers invokers {@link Invoker}
     * @param invocation        the request invoker condition
     * @return                  match condition invokers.
     */
    protected abstract List<Invoker<T>> doList(List<Invoker<T>> availableInvokers, Invocation invocation);


}

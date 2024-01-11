package com.hqy.cloud.rpc.cluster.directory;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.api.ServiceNotifyListener;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.router.RouterFactory;
import com.hqy.cloud.rpc.cluster.router.gray.GrayModeRouterFactory;
import com.hqy.cloud.rpc.cluster.router.hashfactor.HashFactorRouterFactory;
import com.hqy.cloud.rpc.cluster.router.master.MasterNodeRouterFactory;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/4 13:28
 */
public abstract class DynamicDirectory<T> extends AbstractDirectory<T> implements ServiceNotifyListener {
    private static final Logger log = LoggerFactory.getLogger(DynamicDirectory.class);

    protected List<RouterFactory<T>> routerFactories;

    protected final Class<T> serviceType;

    protected volatile boolean forbidden = false;

    private final Registry registry;

    /**
     * Should continue route if directory is empty
     */
    private final boolean shouldFailFast;

    /**
     * Initialization at construction time, assertion not null, and always assign not null value
     */
    protected volatile RpcModel subscribeModel;


    public DynamicDirectory(String providerServiceName, RpcModel rpcModel, Class<T> serviceType, Registry registry) {
        this(providerServiceName, rpcModel, serviceType, registry, List.of(new GrayModeRouterFactory<>(), new HashFactorRouterFactory<>(), new MasterNodeRouterFactory<>()));
    }

    public DynamicDirectory(String providerServiceName, RpcModel rpcModel, Class<T> serviceType, Registry registry, List<RouterFactory<T>> routerFactories) {
        super(providerServiceName, serviceType, rpcModel);
        this.rpcModel = rpcModel;
        this.serviceType = serviceType;
        setRouterFactories(routerFactories);
        this.registry = registry;
        AssertUtil.notNull(getRegistry(), "Registry is null, please check status of Directory.");
        shouldFailFast = true;
    }

    @Override
    protected List<Invoker<T>> doList(List<Invoker<T>> availableInvokers, Invocation invocation) {
        if (forbidden && shouldFailFast) {
            // 1. No service provider 2. Service providers are disabled
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "No provider available from registry " +
                    getRegistry().getRegistryInfo() + " for service " + getProviderServiceName() + " on consumer " +
                    IpUtil.getHostAddress() +
                    ", please check status of providers(disabled, not registered or in blacklist).");
        }

        try {
            return routerChain.route(availableInvokers, invocation);
        } catch (Throwable t) {
            log.error("Failed to execute router: " + getRPCModel() + ", cause: " + t.getMessage(), t);
            return Collections.emptyList();
        }

    }

    @Override
    public boolean isAvailable() {
        if (isDestroyed() || this.forbidden) {
            return false;
        }
        return CollectionUtils.isNotEmpty(getValidInvokers())
                && getValidInvokers().stream().anyMatch(Invoker::isAvailable);
    }

    public void subscribe(RpcModel rpcModel) {
        setSubscribeModel(rpcModel);
        getRegistry().subscribe(buildInstance(rpcModel), this);
    }

    public void unSubscribe(RpcModel rpcModel) {
        setSubscribeModel(null);
        getRegistry().unsubscribe(buildInstance(rpcModel), this);
    }

    public RpcModel getSubscribeModel() {
        return subscribeModel;
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }
        RpcModel rpcModel = getRPCModel();
        Registry registry = getRegistry();
        try {
            if (rpcModel != null && registry != null && registry.isAvailable()) {
                registry.unregister(buildInstance(rpcModel));
            }
        } catch (Throwable t) {
            log.warn("unexpected error when unregister service " + rpcModel.getName() + " from registry: " + registry.getModel(), t);
        }

        // unsubscribe.
        RpcModel subscribeContext = getSubscribeModel();
        try {
            if (subscribeContext != null && registry != null && registry.isAvailable()) {
                registry.unsubscribe(buildInstance(subscribeContext), this);
            }
        } catch (Throwable t) {
            log.warn("unexpected error when unsubscribe service " + subscribeContext.getName() + " from registry: " + registry.getModel(), t);
        }

        synchronized (this) {
            try {
                destroyAllInvokers();
            } catch (Throwable t) {
                log.warn("Failed to destroy service " + getProviderServiceName(), t);
            }
            routerChain.destroy();
            super.destroy(); // must be executed after unsubscribing
        }
    }


    @Override
    public Class<T> getInterface() {
        return serviceType;
    }

    @Override
    public List<Invoker<T>> getAllInvokers() {
        return getInvokers();
    }

    @Override
    public RpcModel getRPCModel() {
        return rpcModel;
    }

    public void setRouterFactories(List<RouterFactory<T>> routerFactories) {
        this.routerFactories = routerFactories;
    }

    public void setSubscribeModel(RpcModel subscribeModel) {
        this.subscribeModel = subscribeModel;
    }

    public Registry getRegistry() {
        return registry;
    }

    protected synchronized void invokersChanged() {
        refreshInvoker();
    }

    /**
     * rpc model building service instance
     * @param rpcModel rpc model, {@link RpcModel}
     * @return ServiceInstance
     */
    protected abstract ServiceInstance buildInstance(RpcModel rpcModel);

    /**
     * destroy all.
     */
    protected abstract void destroyAllInvokers();



}

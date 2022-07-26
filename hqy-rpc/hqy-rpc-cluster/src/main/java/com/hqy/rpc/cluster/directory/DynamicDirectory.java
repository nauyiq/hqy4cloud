package com.hqy.rpc.cluster.directory;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.router.RouterFactory;
import com.hqy.rpc.cluster.router.gray.GrayModeRouterFactory;
import com.hqy.rpc.cluster.router.hashfactor.HashFactorRouterFactory;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/4 13:28
 */
public abstract class DynamicDirectory<T> extends AbstractDirectory<T> implements NotifyListener {

    private static final Logger log = LoggerFactory.getLogger(DynamicDirectory.class);

    protected List<RouterFactory<T>> routerFactories;

    protected final Class<T> serviceType;

    protected volatile boolean forbidden = false;

    private final RegistryFactory factory;

    /**
     * Should continue route if directory is empty
     */
    private final boolean shouldFailFast;

    /**
     * Initialization at construction time, assertion not null, and always assign not null value
     */
    protected volatile RPCModel subscribeContext;


    public DynamicDirectory(String providerServiceName, RPCModel rpcModel, Class<T> serviceType, RegistryFactory registryFactory) {
        this(providerServiceName, rpcModel, serviceType, registryFactory, Arrays.asList(new GrayModeRouterFactory<>(), new HashFactorRouterFactory<>()));
    }

    public DynamicDirectory(String providerServiceName, RPCModel rpcModel, Class<T> serviceType, RegistryFactory registryFactory, List<RouterFactory<T>> routerFactories) {
        super(providerServiceName, serviceType, rpcModel);
        this.consumerContext = rpcModel;
        this.serviceType = serviceType;
        setRouterFactories(routerFactories);
        this.factory = registryFactory;
        AssertUtil.notNull(getRegistry(), "Registry is null, please check status of Directory.");
        shouldFailFast = true;
    }

    @Override
    protected List<Invoker<T>> doList(List<Invoker<T>> availableInvokers) {

        if (forbidden && shouldFailFast) {
            // 1. No service provider 2. Service providers are disabled
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "No provider available from registry " +
                    getRegistry().getRegistryAddress() + " for service " + getProviderServiceName() + " on consumer " +
                    IpUtil.getHostAddress() +
                    ", please check status of providers(disabled, not registered or in blacklist).");
        }

        try {
            return routerChain.route(getConsumerModel(), availableInvokers);
        } catch (Throwable t) {
            log.error("Failed to execute router: " + getConsumerModel() + ", cause: " + t.getMessage(), t);
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

    public void subscribe(RPCModel rpcModel) {
        setSubscribeContext(rpcModel);
        getRegistry().subscribe(rpcModel, this);
    }

    public void unSubscribe(RPCModel rpcModel) {
        setSubscribeContext(null);
        getRegistry().unsubscribe(rpcModel, this);
    }

    public RPCModel getSubscribeContext() {
        return subscribeContext;
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }

        RPCModel rpcModel = getConsumerModel();
        Registry registry = getRegistry();
        try {
            if (rpcModel != null && registry != null && registry.isAvailable()) {
                registry.unregister(rpcModel);
            }
        } catch (Throwable t) {
            log.warn("unexpected error when unregister service " + rpcModel.getName() + " from registry: " + registry.getModel(), t);
        }

        // unsubscribe.
        RPCModel subscribeContext = getSubscribeContext();
        try {
            if (subscribeContext != null && registry != null && registry.isAvailable()) {
                registry.unsubscribe(subscribeContext, this);
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

    public RegistryFactory getFactory() {
        return factory;
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
    public RPCModel getConsumerModel() {
        return consumerContext;
    }

    public void setRouterFactories(List<RouterFactory<T>> routerFactories) {
        this.routerFactories = routerFactories;
    }

    public void setSubscribeContext(RPCModel subscribeContext) {
        this.subscribeContext = subscribeContext;
    }

    public Registry getRegistry() {
        return factory.getRegistry(getConsumerModel());
    }

    protected abstract void destroyAllInvokers();

    protected synchronized void invokersChanged() {
        refreshInvoker();
    }
}

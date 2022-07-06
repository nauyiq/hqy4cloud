package com.hqy.rpc.cluster.directory;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.router.RouterFactory;
import com.hqy.rpc.cluster.router.gray.GrayModeRouterFactory;
import com.hqy.rpc.cluster.router.hashfactor.HashFactorRouterFactory;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
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

    protected List<RouterFactory> routerFactories;

    protected final Class<T> serviceType;

    protected final Metadata consumerMetadata;

    protected volatile boolean forbidden = false;

    protected Registry registry;

    /**
     * Should continue route if directory is empty
     */
    private final boolean shouldFailFast;

    /**
     * Initialization at construction time, assertion not null, and always assign not null value
     */
    protected volatile Metadata subscribeMetadata;


    public DynamicDirectory(String providerServiceName, Metadata consumerMetadata, Class<T> serviceType) {
        this(providerServiceName, consumerMetadata, serviceType, Arrays.asList(new GrayModeRouterFactory(), new HashFactorRouterFactory()));
    }

    public DynamicDirectory(String providerServiceName, Metadata consumerMetadata, Class<T> serviceType, List<RouterFactory> routerFactories) {
        super(providerServiceName, consumerMetadata);
        this.consumerMetadata = consumerMetadata;
        this.serviceType = serviceType;
        setRouterFactories(routerFactories);
        this.registry = setRegistry();
        AssertUtil.notNull(registry, "Registry is null, please check status of Directory.");
        shouldFailFast = true;
    }

    @Override
    protected List<Invoker<T>> doList(List<Invoker<T>> availableInvokers, Invocation invocation) {

        if (forbidden && shouldFailFast) {
            // 1. No service provider 2. Service providers are disabled
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION, "No provider available from registry " +
                    registry.getRegistryAddress() + " for service " + registry.getServiceNameEn() + " on consumer " +
                    IpUtil.getHostAddress() +
                    ", please check status of providers(disabled, not registered or in blacklist).");
        }

        try {
            return routerChain.route(consumerMetadata(), availableInvokers, invocation);
        } catch (Throwable t) {
            log.error("Failed to execute router: " + consumerMetadata() + ", cause: " + t.getMessage(), t);
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

    public void subscribe(Metadata metadata) {
        setSubscribeMetadata(metadata);
        registry.subscribe(metadata, this);
    }

    public void unSubscribe(Metadata metadata) {
        setSubscribeMetadata(null);
        registry.unsubscribe(metadata, this);
    }

    public Metadata getSubscribeMetadata() {
        return subscribeMetadata;
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }

        Metadata metadata = consumerMetadata();
        try {
            if (metadata != null && registry != null && registry.isAvailable()) {
                registry.unregister(metadata);
            }
        } catch (Throwable t) {
            log.warn("unexpected error when unregister service " + metadata.getServiceName() + " from registry: " + registry.getMetadata(), t);
        }

        // unsubscribe.
        Metadata subscribeMetadata = getSubscribeMetadata();
        try {
            if (subscribeMetadata != null && registry != null && registry.isAvailable()) {
                registry.unsubscribe(subscribeMetadata, this);
            }
        } catch (Throwable t) {
            log.warn("unexpected error when unsubscribe service " + subscribeMetadata.getServiceName() + " from registry: " + registry.getMetadata(), t);
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
    public Metadata consumerMetadata() {
        return consumerMetadata;
    }

    public void setRouterFactories(List<RouterFactory> routerFactories) {
        this.routerFactories = routerFactories;
    }

    public void setSubscribeMetadata(Metadata subscribeMetadata) {
        this.subscribeMetadata = subscribeMetadata;
    }

    /**
     * child class implement build Registry
     * @return Registry
     */
    public abstract Registry setRegistry();

    protected abstract void destroyAllInvokers();
}

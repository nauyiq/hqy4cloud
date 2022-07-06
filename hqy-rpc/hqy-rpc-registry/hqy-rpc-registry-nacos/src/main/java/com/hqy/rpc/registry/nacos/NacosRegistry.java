package com.hqy.rpc.registry.nacos;

import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.RegistryNotifier;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.support.FailBackRegistry;
import com.hqy.rpc.registry.nacos.naming.NamingServiceWrapper;
import com.hqy.rpc.registry.nacos.util.NacosInstanceManageUtil;
import com.hqy.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.hqy.rpc.registry.nacos.node.NacosNode.getGroup;

/**
 * Nacos {@link Registry}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:19
 */
public class NacosRegistry extends FailBackRegistry {

    private static final Logger log = LoggerFactory.getLogger(NacosRegistry.class);
    private static final String UP = "UP";

    private final NamingServiceWrapper namingService;
    private final Map<Metadata, EventListener> nacosListeners = MapUtil.newConcurrentHashMap();


    public NacosRegistry(Metadata metadata, NamingServiceWrapper namingService) {
        super(metadata);
        this.namingService = namingService;
    }


    @Override
    public String getServiceNameEn() {
        return getRegistryMetadata().getServiceName();
    }

    @Override
    public List<Metadata> lookup(Metadata metadata) {
        AssertUtil.notNull(metadata, "Failed execute to lookup, metadata is null.");
        AssertUtil.notEmpty(metadata.getServiceName(), "Failed execute to lookup, serviceName is empty.");
        try {
            List<Instance> instances = namingService.selectInstances(metadata.getServiceName(), true);
            return MetadataContext.instancesConvert(getRegistryMetadata().getConnectionInfo(), instances);
        } catch (Throwable t) {
            throw new RpcException("Failed to lookup " + metadata + " from nacos " + getRegistryMetadata() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    public void doRegister(Metadata metadata) {
        AssertUtil.notNull(metadata, "Nacos Registry register failed, metadata is null.");
        try {
            String serviceName = metadata.getServiceName();
            Instance instance = createInstance(metadata);
            namingService.registerInstance(serviceName, getGroup(metadata.getNode()), instance);
        } catch (Throwable cause) {
            throw new RpcException("Failed to register to nacos " + getRegistryMetadata() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doUnregister(Metadata metadata) {
        AssertUtil.notNull(metadata, "Nacos Registry unregister failed, metadata is null.");
        try {
            String serviceName = metadata.getServiceName();
            Instance instance = createInstance(metadata);
            namingService.deregisterInstance(serviceName, getGroup(metadata.getNode()), instance);
        } catch (Throwable cause) {
            throw new RpcException("Failed to unregister to nacos " + getRegistryMetadata() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doSubscribe(Metadata metadata, NotifyListener listener) {
        String serviceName = metadata.getServiceName();
        try {
            List<Instance> instances = new LinkedList<>(namingService.selectInstances(serviceName, true));
            NacosInstanceManageUtil.initOrRefreshServiceInstanceList(serviceName, instances);
            notifySubscriber(metadata, instances, listener);
            subscribeEventListener(serviceName, metadata, listener);
        } catch (Throwable t) {
            throw new RpcException("Failed to subscribe " + metadata + " to nacos " + getRegistryMetadata() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    public void doUnsubscribe(Metadata metadata, NotifyListener listener) {
        String serviceName = metadata.getServiceName();
        try {
            EventListener eventListener = nacosListeners.get(metadata);
            if (Objects.nonNull(eventListener)) {
                namingService.unsubscribe(serviceName, eventListener);
            }
        } catch (Throwable t) {
            throw new RpcException("Failed to doUnsubscribe " + metadata + " to nacos " + getRegistryMetadata() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    protected void notify(Metadata metadata, NotifyListener listener, List<Metadata> metadataList) {
        AssertUtil.notNull(metadata, "Metadata is null.");
        AssertUtil.notNull(listener, "NotifyListener is null.");
        try {
            doNotify(metadata, listener, metadataList);
        } catch (Throwable t) {
            // Record a failed registration request to a failed list
            log.error("Failed to notify addresses for subscribe " + metadata + ", cause: " + t.getMessage(), t);
        }
    }


    @Override
    public boolean isAvailable() {
        return UP.equals(namingService.getServerStatus());
    }

    private void subscribeEventListener(String serviceName, Metadata metadata, NotifyListener listener) throws NacosException {
        EventListener eventListener = nacosListeners.computeIfAbsent(metadata, k -> new NacosRegistryListener(serviceName, metadata, listener));
        namingService.subscribe(serviceName, eventListener);
    }

    /**
     * Notify the Enabled {@link Instance instances} to subscriber.
     * @param metadata  {@link Metadata}
     * @param instances {@link NotifyListener}
     * @param listener  {@link Instance}
     */
    private void notifySubscriber(Metadata metadata, List<Instance> instances, NotifyListener listener) {
        List<Instance> enabledInstances = new LinkedList<>(instances);
        if (enabledInstances.size() > 0) {
            //  Instances
            filterEnabledInstances(enabledInstances);
        }
        List<Metadata> metadataList = MetadataContext.instancesConvert(metadata.getConnectionInfo(), instances);
        NacosRegistry.this.notify(metadata, listener, metadataList);
    }

    private void filterEnabledInstances(Collection<Instance> instances) {
        filterData(instances, Instance::isEnabled);
    }

    private <T> void filterData(Collection<T> collection, NacosDataFilter<T> filter) {
        // remove if not accept
        collection.removeIf(data -> !filter.accept(data));
    }

    private Instance createInstance(Metadata metadata) {
        Instance instance = new Instance();
        instance.setIp(metadata.getHost());
        instance.setPort(metadata.getPort());
        instance.setMetadata(MetadataContext.buildMetadata(metadata.getNode()));
        return instance;
    }

    private class NacosRegistryListener implements EventListener {

        private final Metadata consumerMetadata;

        private final String serviceName;

        private final NotifyListener notifyListener;

        private final RegistryNotifier<List<Instance>> notifier;

        public NacosRegistryListener(String serviceName, Metadata consumerMetadata, NotifyListener listener) {
            this.consumerMetadata = consumerMetadata;
            this.serviceName = serviceName;
            this.notifyListener = listener;
            this.notifier = new RegistryNotifier<List<Instance>>(getMetadata(), NacosRegistry.this.getNotifyDelay()) {
                @Override
                protected void doNotify(List<Instance> rawAddresses) {
                    NacosInstanceManageUtil.initOrRefreshServiceInstanceList(serviceName, rawAddresses);
                    NacosRegistry.this.notifySubscriber(consumerMetadata, rawAddresses, listener);
                }
            };
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof NamingEvent) {
                NamingEvent e = (NamingEvent) event;
                notifier.notify(e.getInstances());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NacosRegistryListener that = (NacosRegistryListener) o;
            return Objects.equals(consumerMetadata, that.consumerMetadata) && Objects.equals(serviceName, that.serviceName) && Objects.equals(notifyListener, that.notifyListener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(consumerMetadata, serviceName, notifyListener);
        }
    }


    /**
     * A filter for Nacos data
     * @since 2.6.5
     */
    private interface NacosDataFilter<T> {

        /**
         * Tests whether or not the specified data should be accepted.
         *
         * @param data The data to be tested
         * @return <code>true</code> if and only if <code>data</code>
         * should be accepted
         */
        boolean accept(T data);

    }





}

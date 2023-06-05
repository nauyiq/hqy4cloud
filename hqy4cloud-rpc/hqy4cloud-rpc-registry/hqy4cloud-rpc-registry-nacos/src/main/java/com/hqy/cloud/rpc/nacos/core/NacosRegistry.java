package com.hqy.cloud.rpc.nacos.core;

import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.registry.RegistryNotifier;
import com.hqy.cloud.rpc.registry.api.NotifyListener;
import com.hqy.cloud.rpc.registry.api.Registry;
import com.hqy.cloud.rpc.registry.api.support.FailBackRegistry;
import com.hqy.cloud.rpc.nacos.naming.NamingServiceWrapper;
import com.hqy.cloud.rpc.nacos.utils.NacosInstanceUtils;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Nacos {@link Registry}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:19
 */
public class NacosRegistry extends FailBackRegistry {

    private static final Logger log = LoggerFactory.getLogger(NacosRegistry.class);

    public static final String NAME = "nacos";
    private final static String UP = "UP";
    private final NamingServiceWrapper namingService;
    private final Map<RPCModel, EventListener> nacosListeners = MapUtil.newConcurrentHashMap();


    public NacosRegistry(RPCModel rpcModel, NamingServiceWrapper namingService) {
        super(rpcModel);
        this.namingService = namingService;
    }


    @Override
    public String getServiceNameEn() {
        return getRegistryRpcContext().getName();
    }

    @Override
    public List<RPCModel> lookup(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "Failed execute to lookup, rpcContext is null.");
        AssertUtil.notEmpty(rpcModel.getName(), "Failed execute to lookup, serviceName is empty.");
        try {
            List<Instance> instances = namingService.selectInstances(rpcModel.getName(), true);
            return NacosInstanceUtils.instancesConvert(getRegistryRpcContext().getRegistryInfo(), rpcModel.getGroup(), instances);
        } catch (Throwable t) {
            throw new RpcException("Failed to lookup " + rpcModel + " from nacos " + getRegistryRpcContext() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    public void doRegister(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "Nacos Registry register failed, rpcContext is null.");
        try {
            String serviceName = rpcModel.getName();
            Instance instance = createInstance(rpcModel);
            namingService.registerInstance(serviceName, rpcModel.getGroup(), instance);
        } catch (Throwable cause) {
            throw new RpcException("Failed to register to nacos " + getRegistryRpcContext() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doUnregister(RPCModel rpcModel) {
        AssertUtil.notNull(rpcModel, "Nacos Registry unregister failed, rpcContext is null.");
        try {
            String serviceName = rpcModel.getName();
            Instance instance = createInstance(rpcModel);
            namingService.deregisterInstance(serviceName, rpcModel.getGroup(), instance);
        } catch (Throwable cause) {
            throw new RpcException("Failed to unregister to nacos " + getRegistryRpcContext() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doSubscribe(RPCModel rpcModel, NotifyListener listener) {
        String serviceName = rpcModel.getName();
        try {
            List<Instance> instances = new LinkedList<>(namingService.selectInstances(serviceName, true));
            notifySubscriber(rpcModel, instances, listener);
            subscribeEventListener(serviceName, rpcModel, listener);
        } catch (Throwable t) {
            throw new RpcException("Failed to subscribe " + rpcModel + " to nacos " + getRegistryRpcContext() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    public void doUnsubscribe(RPCModel rpcModel, NotifyListener listener) {
        String serviceName = rpcModel.getName();
        try {
            EventListener eventListener = nacosListeners.get(rpcModel);
            if (Objects.nonNull(eventListener)) {
                namingService.unsubscribe(serviceName, eventListener);
            }
        } catch (Throwable t) {
            throw new RpcException("Failed to doUnsubscribe " + rpcModel + " to nacos " + getRegistryRpcContext() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    protected void notify(RPCModel rpcModel, NotifyListener listener, List<RPCModel> rpcModels) {
        AssertUtil.notNull(rpcModel, "Metadata is null.");
        AssertUtil.notNull(listener, "NotifyListener is null.");
        try {
            doNotify(rpcModel, listener, rpcModels);
        } catch (Throwable t) {
            // Record a failed registration request to a failed list
            log.error("Failed to notify addresses for subscribe " + rpcModel + ", cause: " + t.getMessage(), t);
        }
    }


    @Override
    public boolean isAvailable() {
        return UP.equals(namingService.getServerStatus());
    }

    private void subscribeEventListener(String serviceName, RPCModel rpcModel, NotifyListener listener) throws NacosException {
        EventListener eventListener = nacosListeners.computeIfAbsent(rpcModel, k -> new NacosRegistryListener(serviceName, rpcModel, listener));
        namingService.subscribe(serviceName, eventListener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Notify the Enabled {@link Instance instances} to subscriber.
     * @param rpcModel  {@link RPCModel}
     * @param instances {@link NotifyListener}
     * @param listener  {@link Instance}
     */
    private void notifySubscriber(RPCModel rpcModel, List<Instance> instances, NotifyListener listener) {
        List<Instance> enabledInstances = new LinkedList<>(instances);
        if (enabledInstances.size() > 0) {
            //  Instances
            filterEnabledInstances(enabledInstances);
        }
        List<RPCModel> rpcModels = NacosInstanceUtils.instancesConvert(rpcModel.getRegistryInfo(), rpcModel.getGroup(),instances);
        NacosRegistry.this.notify(rpcModel, listener, rpcModels);
    }

    private void filterEnabledInstances(Collection<Instance> instances) {
        filterData(instances, Instance::isEnabled);
    }

    private <T> void filterData(Collection<T> collection, NacosDataFilter<T> filter) {
        // remove if not accept
        collection.removeIf(data -> !filter.accept(data));
    }

    private Instance createInstance(RPCModel rpcModel) throws Exception {
        Instance instance = new Instance();
        instance.setServiceName(rpcModel.getName());
        instance.setIp(rpcModel.getHost());
        instance.setPort(rpcModel.getPort());
        instance.setMetadata(NacosInstanceUtils.buildMetadata(rpcModel));
        return instance;
    }

    private class NacosRegistryListener implements EventListener {

        private final RPCModel rpcModel;

        private final String serviceName;

        private final NotifyListener notifyListener;

        private final RegistryNotifier<List<Instance>> notifier;

        public NacosRegistryListener(String serviceName, RPCModel rpcModel, NotifyListener listener) {
            this.rpcModel = rpcModel;
            this.serviceName = serviceName;
            this.notifyListener = listener;
            this.notifier = new RegistryNotifier<List<Instance>>(getModel(), NacosRegistry.this.getNotifyDelay()) {
                @Override
                protected void doNotify(List<Instance> rawAddresses) {
//                    NacosInstanceManageUtil.initOrRefreshServiceInstanceList(serviceName, rawAddresses);
                    NacosRegistry.this.notifySubscriber(rpcModel, rawAddresses, listener);
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
            return Objects.equals(rpcModel, that.rpcModel) && Objects.equals(serviceName, that.serviceName) && Objects.equals(notifyListener, that.notifyListener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rpcModel, serviceName, notifyListener);
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

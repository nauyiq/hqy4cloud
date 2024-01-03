package com.hqy.cloud.registry.nacos.core;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.registry.api.FailedBackRegistry;
import com.hqy.cloud.registry.api.RegistryNotifier;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.api.ServiceNotifyListener;
import com.hqy.cloud.registry.common.exeception.RegisterDiscoverException;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.MetadataInfo;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.converter.MetadataConverter;
import com.hqy.cloud.registry.nacos.Constants;
import com.hqy.cloud.registry.nacos.naming.NamingServiceWrapper;
import com.hqy.cloud.registry.nacos.utils.NacosInstanceConvertUtil;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NacosRegistry.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class NacosRegistry extends FailedBackRegistry {
    private static final Logger log = LoggerFactory.getLogger(NacosRegistry.class);

    private final NamingServiceWrapper namingService;
    private final MetadataConverter metadataConverter;
    private final Map<ApplicationModel, EventListener> nacosListeners = new ConcurrentHashMap<>();

    public NacosRegistry(ApplicationModel model, NamingServiceWrapper namingService, MetadataConverter metadataConverter) {
        super(model);
        AssertUtil.notNull(namingService, "Naming service should not be null.");
        AssertUtil.notNull(metadataConverter, "MetadataConverter should not be null.");
        this.namingService = namingService;
        this.metadataConverter = metadataConverter;
    }

    @Override
    protected ServiceInstance querySelfInstanceInfo() {
        ApplicationModel applicationModel = getModel();
        String applicationName = applicationModel.getApplicationName();
        String group = applicationModel.getGroup();
        RegistryInfo registryInfo = applicationModel.getRegistryInfo();
        try {
            List<Instance> allInstances = namingService.getAllInstances(applicationName, group)
                    .stream().filter(instance -> instance.getIp().equals(applicationModel.getIp()) && instance.getPort() == applicationModel.getPort()).toList();
            if (CollectionUtils.isEmpty(allInstances)) {
                throw new IllegalStateException();
            }
            Instance instance = allInstances.get(0);
            return new NacosServiceInstance(instance, group, registryInfo, metadataConverter.convertMetadataInfo(applicationName, instance.getMetadata()));
        } catch (Throwable cause) {
            throw new RegisterDiscoverException("Not found self instance by " + applicationName + ", nacos " + getRegistryInfo());
        }
    }

    @Override
    protected ServiceInstance queryMasterInstance() {
        ApplicationModel model = getModel();
        String applicationName = model.getApplicationName();
        String group = model.getGroup();
        RegistryInfo registryInfo = getRegistryInfo();
        try {
            List<Instance> instances = namingService.selectInstances(applicationName, group, true);
            List<ServiceInstance> serviceInstances = getServiceInstances(instances, registryInfo, group).stream().filter(ServiceInstance::isMaster).toList();
            if (CollectionUtils.isNotEmpty(serviceInstances)) {
                return serviceInstances.get(0);
            }
            return null;
        } catch (NacosException cause) {
            throw new RegisterDiscoverException("Failed to query master instance to nacos " + getRegistryInfo() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    protected synchronized ServiceInstance doRegister() {
        ApplicationModel model = getModel();
        String applicationName = model.getApplicationName();
        MetadataInfo metadataInfo = model.getMetadataInfo();
        Instance instance = NacosInstanceConvertUtil.convert(model, metadataConverter.convertMap(metadataInfo));
        try {
            namingService.registerInstance(applicationName, model.getGroup(), instance);
        } catch (NacosException cause) {
            throw new RegisterDiscoverException("Failed to register to nacos " + getRegistryInfo() + ", cause: " + cause.getMessage(), cause);
        }
        return new NacosServiceInstance(instance, model.getGroup(), model.getRegistryInfo(), metadataConverter.convertMetadataInfo(applicationName, instance.getMetadata()));
    }

    @Override
    protected synchronized void doUnRegister() {
        ApplicationModel model = getModel();
        unregister(getInstance());
    }

    @Override
    public synchronized void update(ApplicationModel model) throws RuntimeException {
        try {
            ServiceInstance serviceInstance = this.getInstance();
            NacosServiceInstance nacosServiceInstance = (NacosServiceInstance) serviceInstance;
            Instance instance = nacosServiceInstance.getInstance();
            // update params
            NacosInstanceConvertUtil.updateInstance(instance, model, metadataConverter.convertMap(model.getMetadataInfo()));
            // do update to nacos
            namingService.updateInstance(nacosServiceInstance.gerServiceName(), model.getGroup(), instance);
            // reset
            nacosServiceInstance.setInstance(instance);
            nacosServiceInstance.setModel(model);
            super.instance = nacosServiceInstance;
            this.setModel(model);
        } catch (Throwable cause) {
            throw new RegisterDiscoverException("Failed to update to nacos " + getRegistryInfo() + ", cause: " + cause.getMessage(), cause);
        }

    }

    @Override
    public void doRegister(ApplicationModel model) {
        String applicationName = model.getApplicationName();
        MetadataInfo metadataInfo = model.getMetadataInfo();
        Instance instance = NacosInstanceConvertUtil.convert(model, metadataConverter.convertMap(metadataInfo));
        try {
            namingService.registerInstance(applicationName, model.getGroup(), instance);
        } catch (NacosException cause) {
            throw new RegisterDiscoverException("Failed to register to nacos " + getRegistryInfo() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doUnregister(ApplicationModel model) {
        String applicationName = model.getApplicationName();
        MetadataInfo metadataInfo = model.getMetadataInfo();
        Instance instance = NacosInstanceConvertUtil.convert(model, metadataConverter.convertMap(metadataInfo));
        try {
            namingService.deregisterInstance(applicationName, model.getGroup(), instance);
        } catch (NacosException cause) {
            throw new RegisterDiscoverException("Failed to unregister to nacos " + getRegistryInfo() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doSubscribe(ApplicationModel model, ServiceNotifyListener listener) {
        String applicationName = model.getApplicationName();
        String group = model.getGroup();
        try {
            List<Instance> allInstances = namingService.getAllInstances(applicationName, group);
            notifySubscriber(model, allInstances, listener);
            subscribeEventListener(applicationName, model, listener);
        } catch (NacosException cause) {
            throw new RegisterDiscoverException("Failed to subscribe " + model + " to nacos " + getRegistryInfo() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doUnsubscribe(ApplicationModel model, ServiceNotifyListener listener) {
        String applicationName = model.getApplicationName();
        EventListener eventListener = nacosListeners.get(model);
        if (eventListener != null) {
            try {
                namingService.unsubscribe(applicationName, eventListener);
            } catch (NacosException t) {
                throw new RegisterDiscoverException("Failed to doUnsubscribe " + model + " to nacos " + getRegistryInfo() + ", cause: " + t.getMessage(), t);
            }
        }
    }

    @Override
    public String name() {
        return Constants.NAME;
    }


    @Override
    public List<ServiceInstance> lookup(ApplicationModel model) {
        AssertUtil.notNull(model, "Application model of lookUp should not be null.");
        try {
            RegistryInfo registryInfo = getRegistryInfo();
            List<Instance> instances = namingService.selectInstances(model.getApplicationName(), model.getGroup(), true);
            return getServiceInstances(instances, registryInfo, model.getGroup());
        } catch (Throwable t) {
            throw new RegisterDiscoverException("Failed to lookup " + model + " from nacos " + getRegistryInfo() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    public List<ApplicationModel> lookupModels(ApplicationModel model) {
        AssertUtil.notNull(model, "Application model of lookUp should not be null.");
        try {
            RegistryInfo registryInfo = getRegistryInfo();
            List<Instance> instances = namingService.selectInstances(model.getApplicationName(), model.getGroup(), true);
            return instances.stream().map(instance -> NacosInstanceConvertUtil.convert(instance, model.getGroup(), registryInfo,
                    metadataConverter.convertMetadataInfo(model.getApplicationName(), instance.getMetadata()))).toList();
        } catch (Throwable t) {
            throw new RegisterDiscoverException("Failed to lookup " + model + " from nacos " + getRegistryInfo() + ", cause: " + t.getMessage(), t);
        }
    }

    private void subscribeEventListener(String applicationName, ApplicationModel model, ServiceNotifyListener listener) throws NacosException {
        EventListener eventListener = nacosListeners.computeIfAbsent(model, v -> new NacosRegistryListener(applicationName, model, listener));
        namingService.subscribe(applicationName, eventListener);
    }

    /**
     * Notify the Enabled {@link Instance instances} to subscriber.
     * @param model     {@link ApplicationModel}
     * @param instances {@link ServiceNotifyListener}
     * @param listener  {@link Instance}
     */
    private void notifySubscriber(ApplicationModel model, List<Instance> instances, ServiceNotifyListener listener) {
        List<Instance> enabledInstances = new LinkedList<>(instances);
        if (enabledInstances.size() > 0) {
            //  Instances
            filterEnabledInstances(enabledInstances);
        }
        RegistryInfo registryInfo = getRegistryInfo();
        String group = model.getGroup();
        List<ServiceInstance> serviceInstances = getServiceInstances(instances, registryInfo, group);
        this.notify(model, listener, serviceInstances);
    }

    private List<ServiceInstance> getServiceInstances(List<Instance> instances, RegistryInfo registryInfo, String group) {
        List<ServiceInstance> serviceInstances = new ArrayList<>(instances.size());
        for (Instance instance : instances) {
            ServiceInstance serviceInstance = new NacosServiceInstance(instance, group, registryInfo, metadataConverter.convertMetadataInfo(instance.getServiceName(), instance.getMetadata()));
            serviceInstances.add(serviceInstance);
        }
        return serviceInstances;
    }

    private void filterEnabledInstances(Collection<Instance> instances) {
        filterData(instances, Instance::isEnabled);
    }

    private <T> void filterData(Collection<T> collection, NacosDataFilter<T> filter) {
        // remove if not accept
        collection.removeIf(data -> !filter.accept(data));
    }


    public NamingServiceWrapper getNamingService() {
        return namingService;
    }

    private class NacosRegistryListener implements EventListener {
        private final ApplicationModel model;
        private final String applicationName;
        private final ServiceNotifyListener notifyListener;
        private final RegistryNotifier<List<Instance>> notifier;

        public NacosRegistryListener(String applicationName, ApplicationModel model, ServiceNotifyListener notifyListener) {
            this.applicationName = applicationName;
            this.model = model;
            this.notifyListener = notifyListener;
            this.notifier = new RegistryNotifier<>(NacosRegistry.this.getNotifyDelay()) {
                @Override
                protected void doNotify(List<Instance> rawAddresses) {
                    NacosRegistry.this.notifySubscriber(model, rawAddresses, notifyListener);
                }
            };
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof NamingEvent e) {
                notifier.notify(e.getInstances());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NacosRegistryListener that = (NacosRegistryListener) o;
            return Objects.equals(model, that.model) && Objects.equals(applicationName, that.applicationName) && Objects.equals(notifyListener, that.notifyListener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(model, applicationName, notifyListener);
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

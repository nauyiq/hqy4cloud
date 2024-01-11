package com.hqy.cloud.rpc.thrift.client;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.api.support.ApplicationServiceInstance;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.directory.DynamicDirectory;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import com.hqy.cloud.rpc.thrift.client.commonpool.MultiplexThriftClientTargetPooled;
import com.hqy.cloud.rpc.thrift.client.protocol.ThriftInvoker;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * com.hqy.cloud.rpc.thrift.client.ThriftDynamicDirectory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/4
 */
public class ThriftDynamicDirectory<T> extends DynamicDirectory<T> {
    private static final Logger log = LoggerFactory.getLogger(ThriftDynamicDirectory.class);

    private final MultiplexThriftClientTargetPooled<T> pooled;

    public ThriftDynamicDirectory(String providerServiceName,
                                  RpcModel rpcModel, Class<T> serviceType,
                                  ThriftClientManagerWrapper clientManager,
                                  Registry registry,
                                  ExecutorRepository executorRepository) {
        super(providerServiceName, rpcModel, serviceType, registry);
        //invokers pooled.
        this.pooled = new MultiplexThriftClientTargetPooled<>(rpcModel, providerServiceName, serviceType, clientManager, executorRepository);
        //must final init invokers
        synchronized (getRegistry()) {
            log.info("Start new com.hqy.cloud.rpc.thrift.client.ThriftDynamicDirectory, notify and subscribe.");
            try {
                subscribeAndNotify(providerServiceName, registry);
            } catch (Throwable t) {
                log.warn("Failed execute to registry subscribe,  metadata {} from registry {}", this.rpcModel, registry);
            }
        }
    }

    private void subscribeAndNotify(String providerServiceName, Registry registry) {
        ApplicationModel model = getModel();
        //create provider rpc model.
        //does not represent a specific remote service
        ApplicationModel lookUpModel = ApplicationModel.of(providerServiceName, model.getNamespace(), model.getGroup());
        //query rpc provider instance from registry.
        List<ServiceInstance> serviceInstances = registry.lookup(lookUpModel);
        //notify.
        notify(serviceInstances);
        //do subscribe.
        registry.subscribe(new ApplicationServiceInstance(lookUpModel), this);
    }

    @Override
    public void notify(List<ServiceInstance> instances) {
        if (isDestroyed()) {
            return;
        }
        List<RpcModel> models = instances.stream().filter(Objects::nonNull)
                .map(instance -> {
                    ApplicationServiceInstance serviceInstance = (ApplicationServiceInstance) instance;
                    ApplicationModel model = serviceInstance.getApplicationModel();
                    return new RpcModel(model);
                })
                .collect(Collectors.toList());
        refreshOverrideAndInvoker(models);
    }


    private synchronized void refreshOverrideAndInvoker(List<RpcModel> rpcModels) {
        refreshInvoker(rpcModels);
    }

    private void refreshInvoker(List<RpcModel> rpcModels) {
        AssertUtil.notNull(rpcModels, "rpcContexts should not be null.");
        if (CollectionUtils.isEmpty(rpcModels)) {
            this.forbidden = true;
            routerChain.setInvokers(Collections.emptyList());
            destroyInvokers();
            pooled.close();
        } else {
            this.forbidden = false;
            List<Invoker<T>> invokers = toInvokers(rpcModels);
            this.setInvokers(invokers);
            //
            routerChain.setInvokers(invokers);
            pooled.refreshObjectPooled(invokers);
        }
    }

    private List<Invoker<T>> toInvokers(List<RpcModel> rpcModels) {
        return rpcModels.stream().map(rpcModel -> {
            try {
                return new ThriftInvoker<>(getInterface(), rpcModel, getRPCModel(), pooled);
            } catch (Exception e) {
                log.error("Create ThriftInvoker error, rpcModel {}", rpcModel, e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    @Override
    protected ServiceInstance buildInstance(RpcModel rpcModel) {
        ApplicationModel model = rpcModel.getModel();
        return new ApplicationServiceInstance(model);
    }

    @Override
    protected void destroyAllInvokers() {
        destroyInvokers();
        pooled.close();
    }

}

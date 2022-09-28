package com.hqy.rpc.client.thrift;

import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.client.thrift.commonpool.MultiplexThriftClientTargetPooled;
import com.hqy.rpc.client.thrift.protocol.ThriftInvoker;
import com.hqy.rpc.client.thrift.support.ThriftClientManagerWrapper;
import com.hqy.rpc.cluster.directory.DynamicDirectory;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ThriftDynamicDirectory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/4 17:53
 */
public class ThriftDynamicDirectory<T> extends DynamicDirectory<T> {

    private final MultiplexThriftClientTargetPooled<T> pooled;

    private static final Logger log = LoggerFactory.getLogger(ThriftDynamicDirectory.class);

    public ThriftDynamicDirectory(String providerServiceName, RPCModel rpcModel, Class<T> serviceType, ThriftClientManagerWrapper clientManagerWrapper, RegistryFactory factory) {
        super(providerServiceName, rpcModel, serviceType, factory);
        //invokers pooled.
        this.pooled = new MultiplexThriftClientTargetPooled<>(rpcModel, serviceType, clientManagerWrapper);
        //must final init invokers
        synchronized (getFactory()) {
            log.info("Start new ThriftDynamicDirectory, notify and subscribe.");
            try {
                subscribeAndNotify(providerServiceName, factory);
            } catch (Throwable t) {
                log.warn("Failed execute to registry subscribe,  metadata {} from registry {}", consumerRpcModel,  factory.getRegistry(consumerRpcModel));
            }
        }

    }

    private void subscribeAndNotify(String providerServiceName, RegistryFactory factory) {
        Registry registry = factory.getRegistry(consumerRpcModel);
        //create provider rpc model.
        //does not represent a specific remote service
        RPCModel rpcModel = new RPCModel(providerServiceName, 0, consumerRpcModel.getGroup(),consumerRpcModel.getRegistryInfo(), null);
        //query rpc provider instance from registry.
        List<RPCModel> rpcModels = registry.lookup(rpcModel);
        //notify.
        notify(rpcModels);
        //do subscribe.
        registry.subscribe(rpcModel, this);
    }


    @Override
    public void notify(List<RPCModel> rpcModels) {
        if (isDestroyed()) {
            return;
        }
        rpcModels = rpcModels.stream().filter(Objects::nonNull).collect(Collectors.toList());
        refreshOverrideAndInvoker(rpcModels);
    }

    private synchronized void refreshOverrideAndInvoker(List<RPCModel> rpcModels) {
        refreshInvoker(rpcModels);
    }

    private void refreshInvoker(List<RPCModel> rpcModels) {
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
            routerChain.setInvokers(invokers);
            pooled.refreshObjectPooled(invokers);
        }
    }

    private List<Invoker<T>> toInvokers(List<RPCModel> rpcModels) {
        return rpcModels.stream().map(rpcContext -> {
            try {
                return new ThriftInvoker<>(getInterface(), rpcContext, getConsumerModel(), pooled);
            } catch (Exception e) {
                log.error("Create ThriftInvoker error, rpcContext {}", rpcContext, e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }



    @Override
    protected void destroyAllInvokers() {
        destroyInvokers();
    }

}

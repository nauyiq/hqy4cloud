package com.hqy.cloud.rpc.thrift.commonpool;

import cn.hutool.core.map.MapUtil;
import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.facebook.swift.service.ThriftMethodHandler;
import com.hqy.cloud.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.thrift.ThriftNiftyFramedClientUtils;
import com.hqy.cloud.rpc.thrift.support.ThriftClientManagerWrapper;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base key{@link RPCServerAddress} for pooled Object factory.
 * <T> Thrift client for service proxy Object. {@link ThriftMethodHandler}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/12 15:24
 */
public class ThriftClientTargetBaseKeyedFactory<T> extends BaseKeyedPooledObjectFactory<RPCServerAddress, T> {

    private static final Logger log = LoggerFactory.getLogger(ThriftClientTargetBaseKeyedFactory.class);

    /**
     * key: microServiceName
     */
    private static final Map<String, Map<RPCServerAddress, FramedClientConnector>> RPC_SERVICE_CONNECTOR_MAP = MapUtil.newConcurrentHashMap();

    private final String serviceName;

    private final Class<T> rpcInterfaceClass;


    private final ThriftClientManagerWrapper clientManagerWrapper;

    public ThriftClientTargetBaseKeyedFactory(String serviceName, Class<T> rpcInterfaceClass, ThriftClientManagerWrapper clientManagerWrapper) {
        this.rpcInterfaceClass = rpcInterfaceClass;
        this.clientManagerWrapper = clientManagerWrapper;
        this.serviceName = serviceName;
    }

    public void refreshFramedClientConnectorMap(List<Invoker<T>> invokers) {
        iniFramedClientConnectorMap(invokers);
    }

    private void iniFramedClientConnectorMap(List<Invoker<T>> invokers) {
        if (CollectionUtils.isEmpty(invokers)) {
            RPC_SERVICE_CONNECTOR_MAP.put(serviceName, MapUtil.newConcurrentHashMap(0));
        }
        Map<RPCServerAddress, FramedClientConnector> map = MapUtil.newConcurrentHashMap(invokers.size());
        for (Invoker<T> invoker : invokers) {
            RPCServerAddress serverAddress = invoker.getModel().getServerAddress();
            FramedClientConnector framedClientConnector = ThriftNiftyFramedClientUtils.createFramedClientConnector(serverAddress);
            map.put(serverAddress, framedClientConnector);
        }
        RPC_SERVICE_CONNECTOR_MAP.put(serviceName, map);
    }

    public String gerServiceInfo(T service) {
        NiftyClientChannel clientChannel = clientManagerWrapper.getClientChannel(service);
        if (Objects.isNull(clientChannel)) {
            return String.format("Invalid service: [%s], NiftyClientChannel is null", service.getClass().getSimpleName());
        }
        return ThriftNiftyFramedClientUtils.printfChannelInfo(clientChannel);
    }

    @Override
    public T create(RPCServerAddress serverAddress) throws Exception {
        AssertUtil.notNull(serverAddress, "RPCServerAddress should not be null.");

        Map<RPCServerAddress, FramedClientConnector> rpcServerAddressFramedClientConnectorMap = RPC_SERVICE_CONNECTOR_MAP.get(serviceName);
        FramedClientConnector connector = rpcServerAddressFramedClientConnectorMap.get(serverAddress);
        if (connector == null) {
            throw new NoAvailableProviderException("No available connectors for this rpc service "
                    + rpcInterfaceClass.getName() + ", rpc server address " + serverAddress + ".");
        }
        return clientManagerWrapper.createClient(connector, rpcInterfaceClass);
    }

    @Override
    public PooledObject<T> wrap(T target) {
        return new DefaultPooledObject<>(target);
    }

    @Override
    public void destroyObject(RPCServerAddress key, PooledObject<T> p) throws Exception {
        NiftyClientChannel clientChannel = clientManagerWrapper.getClientChannel(p.getObject());
        if (Objects.isNull(clientChannel)) {
            return;
        }
        if (clientChannel.getNettyChannel().isConnected()) {
            clientChannel.close();
            log.info("Destroy object, close client channel {}", ThriftNiftyFramedClientUtils.printfChannelInfo(clientChannel));
        }
        Map<RPCServerAddress, FramedClientConnector> rpcServerAddressFramedClientConnectorMap = RPC_SERVICE_CONNECTOR_MAP.get(serviceName);
        rpcServerAddressFramedClientConnectorMap.remove(key);
    }

    /**
     * 连接池关闭时 清楚缓存
     */
    public void closeConnectionCache(String serviceName) {
        RPC_SERVICE_CONNECTOR_MAP.remove(serviceName);
    }
}

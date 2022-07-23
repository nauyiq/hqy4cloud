package com.hqy.rpc.client.thrift.commonpool;

import cn.hutool.core.map.MapUtil;
import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.facebook.swift.service.ThriftMethodHandler;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.client.thrift.support.ThriftClientManagerWrapper;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.client.thrift.ThriftNiftyFramedClientUtils;
import com.hqy.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.jboss.netty.channel.Channel;
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

    private final Class<T> rpcInterfaceClass;

    private Map<RPCServerAddress, FramedClientConnector> framedClientConnectorMap;

    private final ThriftClientManagerWrapper clientManagerWrapper;

    public ThriftClientTargetBaseKeyedFactory(Class<T> rpcInterfaceClass, List<Invoker<T>> invokers, ThriftClientManagerWrapper clientManagerWrapper) {
        this.rpcInterfaceClass = rpcInterfaceClass;
        this.clientManagerWrapper = clientManagerWrapper;
        this.framedClientConnectorMap = iniFramedClientConnectorMap(invokers);
    }

    private Map<RPCServerAddress, FramedClientConnector> iniFramedClientConnectorMap(List<Invoker<T>> invokers) {
        if (CollectionUtils.isEmpty(invokers)) {
            return MapUtil.newConcurrentHashMap();
        }
        Map<RPCServerAddress, FramedClientConnector> map = MapUtil.newConcurrentHashMap(invokers.size());
        for (Invoker<T> invoker : invokers) {
            RPCServerAddress serverAddress = invoker.getModel().getServerAddress();
            FramedClientConnector framedClientConnector = ThriftNiftyFramedClientUtils.createFramedClientConnector(serverAddress);
            map.put(serverAddress, framedClientConnector);
        }
        return framedClientConnectorMap;
    }

    public void refreshFramedClientConnectorMap(List<Invoker<T>> invokers) {
        this.framedClientConnectorMap = iniFramedClientConnectorMap(invokers);
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

        FramedClientConnector connector = framedClientConnectorMap.get(serverAddress);
        if (framedClientConnectorMap.isEmpty() || connector == null) {
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
    }
}

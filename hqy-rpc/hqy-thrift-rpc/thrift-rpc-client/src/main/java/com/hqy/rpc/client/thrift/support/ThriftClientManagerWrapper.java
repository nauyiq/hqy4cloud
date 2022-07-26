package com.hqy.rpc.client.thrift.support;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.nifty.client.NiftyClientChannel;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.facebook.swift.service.ThriftClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * ThriftClientManagerWrapper.
 * @see ThriftClientManager
 * @author qiyuan.hong
 * @date 2022-07-06 23:18
 */
public class ThriftClientManagerWrapper {

    private static final Logger log = LoggerFactory.getLogger(ThriftClientManagerWrapper.class);

    private final ThriftClientManager clientManager;

    public ThriftClientManagerWrapper(ThriftClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public <T> T createClient(FramedClientConnector connector, Class<T> serviceClass) throws Exception {
        return clientManager.createClient(connector, serviceClass).get();
    }

    public <T> NiftyClientChannel getClientChannel(T t) {
        return (NiftyClientChannel) clientManager.getRequestChannel(t);
    }


    public ThriftClientManager getClientManager() {
        return clientManager;
    }
}

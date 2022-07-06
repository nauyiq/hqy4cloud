package com.hqy.rpc.client.thrift;

import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.facebook.swift.service.ThriftClientManager;
import com.hqy.rpc.common.config.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @date 2022-07-06 23:18
 */
public class ThriftClientManagerWrapper {

    private static final Logger log = LoggerFactory.getLogger(ThriftClientManagerWrapper.class);

    private final ThriftClientManager clientManager;

    public static final ThriftClientManagerWrapper CLIENT = new ThriftClientManagerWrapper();

    private ThriftClientManagerWrapper() {
        this(Runtime.getRuntime().availableProcessors() * 4);
    }

    @SuppressWarnings("all")
    private ThriftClientManagerWrapper(int workerThreadCount) {
        Set<ThriftClientEventHandler> eventHandlers = Collections.emptySet();
        if (EnvironmentConfig.getInstance().isRPCCollection() ||EnvironmentConfig.getInstance().isRPCCallChainPersistence()) {
            eventHandlers.add(new ThriftClientStatsEventHandler());
        }
        ThriftCodecManager codecManager = new ThriftCodecManager();
        NettyClientConfig config = NettyClientConfig.newBuilder().setWorkerThreadCount(workerThreadCount).build();
        NiftyClient client = new NiftyClient(config);
        this.clientManager = new ThriftClientManager(codecManager, client, eventHandlers);
    }

    public ThriftClientManagerWrapper(ThriftClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public ThriftClientManager getClientManager() {
        return clientManager;
    }
}

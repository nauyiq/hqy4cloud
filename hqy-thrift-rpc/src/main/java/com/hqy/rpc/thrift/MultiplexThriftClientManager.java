package com.hqy.rpc.thrift;

import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.facebook.swift.service.ThriftClientManager;
import com.hqy.rpc.handler.ThriftClientStatsEventHandler;
import com.hqy.rpc.regist.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 单例类 实现一个进程中NIO客户端复用。 提升性能，减少不必要的Netty客户端
 * @author qy
 * @date  2021-08-18 10:44
 */
public class MultiplexThriftClientManager {

    private static final Logger log = LoggerFactory.getLogger(MultiplexThriftClientManager.class);

    private final ThriftClientManager clientManager;

    /**
     * 工作线程数
     */
    private static final int WORKER_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 4;

    private MultiplexThriftClientManager() {
        Set<ThriftClientEventHandler> eventHandlers = new HashSet<>();
        if (EnvironmentConfig.getInstance().isRPCCollection() || EnvironmentConfig.getInstance().isRPCCallChainPersistence()) {
            eventHandlers.add(new ThriftClientStatsEventHandler());
        }
        //NIO线路复用！！！！
        ThriftCodecManager codecManager = new ThriftCodecManager();
        NettyClientConfig clientConfig = NettyClientConfig.newBuilder().setWorkerThreadCount(WORKER_THREAD_COUNT).build();
        log.info("@@@ WorkerThreadCount={}", WORKER_THREAD_COUNT);
        NiftyClient client = new NiftyClient(clientConfig);
        //NIO线路复用！
        this.clientManager = new ThriftClientManager(codecManager, client, eventHandlers);
    }

    private static final MultiplexThriftClientManager INSTANCE = new MultiplexThriftClientManager();

    public static MultiplexThriftClientManager getInstance() {
        return INSTANCE;
    }

    public static ThriftClientManager getThriftClientManager() {
        return INSTANCE.clientManager;
    }

}

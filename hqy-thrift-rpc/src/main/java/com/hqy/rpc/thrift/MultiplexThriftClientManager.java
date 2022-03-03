package com.hqy.rpc.thrift;

import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.facebook.swift.service.ThriftClientManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 单例类 实现一个进程中NIO客户端复用。 提升性能，减少不必要的Netty客户端
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-18 10:44
 */
@Slf4j
public class MultiplexThriftClientManager {

    private final ThriftClientManager clientManager;

    /**
     * 工作线程数
     */
    private static final int WORKER_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 4;

    private MultiplexThriftClientManager() {
        Set<ThriftClientEventHandler> eventHandlers = new HashSet<>();
        //NIO线路复用！！！！
        ThriftCodecManager codecManager = new ThriftCodecManager();
        NettyClientConfig clientConfig = NettyClientConfig.newBuilder().setWorkerThreadCount(WORKER_THREAD_COUNT).build();
        log.info("### workerThreadCount={}", WORKER_THREAD_COUNT);
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

package com.hqy.cloud.rpc.thrift.server;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.api.Environment;
import com.hqy.cloud.rpc.model.RpcServerAddress;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.model.RpcServiceInfo;
import com.hqy.cloud.rpc.starter.server.AbstractRpcServer;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import com.hqy.cloud.thrift.handler.ThriftServerContextEventHandler;
import com.hqy.cloud.util.AssertUtil;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * ThriftRpcServer, to starting thrift server
 * @see com.facebook.swift.service.ThriftServer
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/4
 */
public class ThriftRpcServer extends AbstractRpcServer {
    private static final Logger log = LoggerFactory.getLogger(ThriftRpcServer.class);

    private volatile ThriftServer thriftServer;
    private volatile ExecutorRepository repository;
    private final ThriftServerModel thriftServerModel;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public ThriftRpcServer(ThriftServerModel thriftServerModel) {
        super(thriftServerModel.getThriftRpcServices());
        AssertUtil.notNull(thriftServerModel, "Thrift server model should not be null.");
        this.thriftServerModel = thriftServerModel;
    }

    @Override
    public void start() throws RpcException {
        if (this.thriftServer != null && !this.thriftServer.isRunning()) {
            this.thriftServer.start();
        }
    }

    @Override
    public void initialize() {
        if (initialized.compareAndSet(false, true)) {
            // Get the executor repository
            this.repository =  BeanRepository.getInstance().getBean(ExecutorRepository.class);
            AssertUtil.notNull(this.repository, "Not found executorRepository from bean repository.");

            // init thrift rpc server
            initThriftServer();

            // setting rpc metadata
            List<RpcServiceInfo> rpcServiceInfos = getRegistryRpcServices().stream().map(rpcService -> new RpcServiceInfo(rpcService.getClass().getName(), rpcService.revision())).toList();
            getModel().setRpcServiceInfo(rpcServiceInfos);
        }
    }



    private void initThriftServer() {
        ThriftServerProperties properties = this.thriftServerModel.getThriftServerProperties();
        List<ThriftServerContextHandleService> handlers = thriftServerModel.getServerThriftEventHandlers();
        AssertUtil.notNull(properties, "Failed execute to create thrift server, threadServerProperties should not be null.");
        AssertUtil.notNull(handlers, "Failed execute to create thrift server, serverThriftEventHandlers should not be null.");

        try {
            definitionServerNettyThreadProperties(properties);
            thriftServer = createThriftServer(repository, properties, Collections.singletonList(new ThriftServerContextEventHandler(handlers)));
        } catch (Throwable cause) {
            log.warn("Failed execute to create rpc server, cause: {}", cause.getMessage(), cause);
            throw new RpcException(RpcException.REGISTRY_EXCEPTION, cause);
        }
    }

    private ThriftServer createThriftServer(ExecutorRepository repository, ThriftServerProperties properties, List<ThriftServerContextEventHandler> serverThriftEventHandlers) {
        //netty logger factory.
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        // netty executor.
        int boosThreadCount = properties.getBoosThreads();
        int workerThreadCount = properties.getWorkerThreads();
        int rpcPort = getModel().getServerAddress().getPort();
        log.info("Start to create thrift netty server, boosThreadCount {}, workerThreadCount {}, binding rpc port {}", boosThreadCount, workerThreadCount, rpcPort);

        //netty thread pool.
        ExecutorService boos = repository.createExecutorIfAbsent(EVENT_LOOP_BOSS_POOL_NAME, boosThreadCount);
        ExecutorService worker = repository.createExecutorIfAbsent(EVENT_LOOP_WORKER_POOL_NAME, workerThreadCount);
        ExecutorService logicWorker = repository.createExecutorIfAbsent(EVENT_LOOP_LOGIC_POOL_NAME, workerThreadCount);

        final NettyServerConfig serverConfig = NettyServerConfig.newBuilder().setBossThreadExecutor(boos)
                .setBossThreadCount(boosThreadCount)
                .setWorkerThreadExecutor(worker)
                .setWorkerThreadCount(workerThreadCount).build();
        // register rpc interface services.
        ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(), serverThriftEventHandlers, getRegistryRpcServices());
        ThriftServerDef serverDef = ThriftServerDef.newBuilder().listen(rpcPort).withProcessor(processor).using(logicWorker).build();

        //thrift rpc server.
        return new ThriftServer(serverConfig, serverDef);
    }

    private void definitionServerNettyThreadProperties(ThriftServerProperties properties) {
        int workerThreads = properties.getWorkerThreads();
        int minIdle = properties.getThreadMinIdle();
        int size = getRegistryRpcServices().size();

        if (size <= 1) {
            //fewer rpc interface instance, reduce io worker thread number.
            workerThreads = Math.max(workerThreads / minIdle, minIdle);
        } else if (Environment.FLAG_IO_INTENSIVE_RPC_SERVICE){
            // IO intensive rpc service, double io worker thread number.
            workerThreads = workerThreads * 2;
        }
        properties.setWorkerThreads(workerThreads);
    }

    @Override
    protected void doDestroy() {
        try {
            thriftServer.close();
            if (repository != null) {
                repository.destroyAll();
            }
        } catch (Throwable cause) {
            log.error("Error happen on destroy rpc server.", cause);
        }
    }

    @Override
    public RpcModel getModel() {
        return thriftServerModel.getRpcModel();
    }

    @Override
    public RpcServerAddress getServerAddr() {
        return getModel().getServerAddress();
    }



}

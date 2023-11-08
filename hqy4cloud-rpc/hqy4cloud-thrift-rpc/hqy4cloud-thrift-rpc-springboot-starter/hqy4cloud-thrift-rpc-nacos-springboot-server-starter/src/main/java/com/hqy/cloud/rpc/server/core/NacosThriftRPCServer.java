package com.hqy.cloud.rpc.server.core;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.rpc.config.deploy.AbstractRPCServer;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import com.hqy.cloud.thrift.handler.ThriftServerContextEventHandler;
import com.hqy.cloud.thrift.core.ThriftServerModel;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.NetUtils;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * NacosThriftRPCServer.
 * @see com.facebook.swift.service.ThriftServer
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 16:58
 */
public class NacosThriftRPCServer extends AbstractRPCServer {
    private static final Logger log = LoggerFactory.getLogger(NacosThriftRPCServer.class);

    private final int port;
    private final RPCServerAddress serverAddress;
    private volatile ExecutorRepository repository;
    private volatile ThriftServer thriftServer;
    private final ThriftServerModel thriftServerModel;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public NacosThriftRPCServer(int port, ThriftServerModel thriftServerModel) {
        super(thriftServerModel.getThriftRpcServices());
        AssertUtil.notNull(thriftServerModel, "ThriftServerModel should not be null.");
        this.port = port;
        this.thriftServerModel = thriftServerModel;
        this.serverAddress = initRpcServerAddr(thriftServerModel.getThriftServerProperties());
    }

    private RPCServerAddress initRpcServerAddr(ThriftServerProperties thriftServerProperties) {
        int pid = NetUtils.getProgramId();
        String host = IpUtil.getHostAddress();
        checkCreateThriftServerCondition(thriftServerProperties);
        log.info("Create ThriftServer begin, host {} | begin binding port {} | pid {}.", host, thriftServerProperties.getRpcPort(), pid);
        return new RPCServerAddress(thriftServerProperties.getRpcPort(), host, pid);
    }

    @Override
    public RPCServerAddress getServerAddr() {
        return this.serverAddress;
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
    public void initialize(ExecutorRepository repository) {
        if (initialized.compareAndSet(false, true)) {
            this.repository = repository;
            this.thriftServer = initThriftServer(repository);
            this.thriftServer.start();
        }
    }

    public boolean isInit() {
        return initialized.get();
    }

    private ThriftServer initThriftServer(ExecutorRepository repository) throws RpcException {
        ThriftServerProperties threadServerProperties = thriftServerModel.getThriftServerProperties();
        List<ThriftServerContextHandleService> serverThriftEventHandlers = thriftServerModel.getServerThriftEventHandlers();
        AssertUtil.notNull(threadServerProperties, "Failed execute to create thrift server, threadServerProperties should not be null.");
        AssertUtil.notNull(serverThriftEventHandlers, "Failed execute to create thrift server, serverThriftEventHandlers should not be null.");

        ThriftServer thriftServer = null;
        try {
            definitionServerNettyThreadProperties(threadServerProperties);

            thriftServer = initThriftServer(repository, threadServerProperties, Collections.singletonList(new ThriftServerContextEventHandler(serverThriftEventHandlers)));
            if (!thriftServer.isRunning()) {
                thriftServer.start();
            }
        } catch (Throwable cause) {
            log.warn("Failed execute to create rpc server, cause: {}", cause.getMessage(), cause);
            ConfigurableApplicationContext cyx = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
            cyx.close();
        }


        return thriftServer;
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

    private ThriftServer initThriftServer(ExecutorRepository repository, ThriftServerProperties properties, List<ThriftEventHandler> serverThriftEventHandlers) {
        //netty logger factory.
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        // netty executor.
        int boosThreadCount = properties.getBoosThreads();
        int workerThreadCount = properties.getWorkerThreads();
        int rpcPort = this.serverAddress.getPort();
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

    private void checkCreateThriftServerCondition(ThriftServerProperties properties) {
        AssertUtil.isTrue(CommonSwitcher.ENABLE_THRIFT_SERVER_BEAN.isOn(), "Common switch should not be create thrift server.");
        int rpcPort = getEnableRpcPort(properties);
        properties.setRpcPort(rpcPort);
    }

    private int getEnableRpcPort(ThriftServerProperties properties) {
        int rpcPort = properties.getRpcPort();
        if (rpcPort == 0) {
            rpcPort = port + 10000;
        }
        int connectRetryTime = properties.getConnectRetryTime();
        int i = 0;
        while (NetUtils.isPortUsing(rpcPort)) {
            log.warn("@@@ The server port already bind! retry new port!!! [{}]", rpcPort);
            i++;
            if(i == connectRetryTime){
                throw new RpcException("Port is using ! Server failed start after MAX_RETRY_TIMES:" + connectRetryTime);
            }else{
                rpcPort = rpcPort + 4;
            }
        }
        return rpcPort;
    }


}

package com.hqy.rpc.server.thrift;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.api.server.RPCServer;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.common.config.EnvironmentConfig;
import com.hqy.rpc.thrift.handler.server.ThriftServerContextEventHandler;
import com.hqy.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.rpc.thrift.handler.support.ThriftServerModel;
import com.hqy.rpc.thrift.support.ThriftServerProperties;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import com.hqy.util.NetUtils;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.NamedThreadFactory;
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

import static com.hqy.rpc.common.CommonConstants.EVENT_LOOP_BOSS_POOL_NAME;
import static com.hqy.rpc.common.CommonConstants.EVENT_LOOP_WORKER_POOL_NAME;

/**
 * ThriftServerWrapper.
 * @see com.facebook.swift.service.ThriftServer
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 16:58
 */
public class ThriftServerWrapper implements RPCServer {

    private static final Logger log = LoggerFactory.getLogger(ThriftServerWrapper.class);

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    /**
     * rpc provider server address.
     */
    private RPCServerAddress serverAddress;

    /**
     * rpc provider server.
     */
    private final ThriftServer thriftServer;

    /**
     * create thriftServer configuration.
     */
    private final ThriftServerModel thriftServerModel;

    public ThriftServerWrapper(ThriftServerModel thriftServerModel) {
        AssertUtil.notNull(thriftServerModel, "ThriftServerModel should not be null.");
        this.thriftServerModel = thriftServerModel;
        this.thriftServer = createThriftServer();
    }

    @Override
    public RPCServerAddress getServerAddr() {
        return this.serverAddress;
    }

    @Override
    public List<RPCService> getRegistryRpcServices() {
        return thriftServerModel.getThriftRpcServices();
    }

    @Override
    public boolean isDestroy() {
        return destroyed.get();
    }

    @Override
    public synchronized void destroy() {
        if (destroyed.get()) {
            return;
        }
        //TODO notify consumer ? or unsubscribe from registry?
        thriftServer.close();
        destroyed.compareAndSet(false, true);
    }

    private ThriftServer createThriftServer() throws RpcException {
        ThriftServerProperties threadServerProperties = thriftServerModel.getThriftServerProperties();
        List<ThriftServerContextHandleService> serverThriftEventHandlers = thriftServerModel.getServerThriftEventHandlers();
        AssertUtil.notNull(threadServerProperties, "Failed execute to create thrift server, threadServerProperties should not be null.");
        AssertUtil.notNull(serverThriftEventHandlers, "Failed execute to create thrift server, serverThriftEventHandlers should not be null.");

        int pid = NetUtils.getProgramId();
        String host = IpUtil.getHostAddress();
        log.info("Create ThriftServer begin, host {} | begin binding port {} | pid {}.", host, threadServerProperties.getRpcPort(), pid);

        ThriftServer thriftServer = null;
        try {
            checkCreateThriftServerCondition(threadServerProperties);

            definitionServerNettyThreadProperties(threadServerProperties);

            thriftServer = createThriftServer(threadServerProperties, Collections.singletonList(new ThriftServerContextEventHandler(serverThriftEventHandlers)));
            if (!thriftServer.isRunning()) {
                thriftServer.start();
            }
        } catch (Throwable cause) {
            log.warn("Failed execute to create rpc server, cause: {}", cause.getMessage(), cause);
            ConfigurableApplicationContext cyx = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
            cyx.close();
        }

        this.serverAddress = new RPCServerAddress(threadServerProperties.getRpcPort(), host, pid);
        return thriftServer;
    }

    private void definitionServerNettyThreadProperties(ThriftServerProperties properties) {
        int workerThreads = properties.getWorkerThreads();
        int minIdle = properties.getThreadMinIdle();
        int size = getRegistryRpcServices().size();

        if (size <= 1) {
            //fewer rpc interface instance, reduce io worker thread number.
            workerThreads = Math.max(workerThreads / minIdle, minIdle);
        } else if (EnvironmentConfig.FLAG_IO_INTENSIVE_RPC_SERVICE){
            // IO intensive rpc service, double io worker thread number.
            workerThreads = workerThreads * 2;
        }
        properties.setWorkerThreads(workerThreads);
    }

    private ThriftServer createThriftServer(ThriftServerProperties properties, List<ThriftEventHandler> serverThriftEventHandlers) {
        //netty logger factory.
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        // netty executor.
        int boosThreadCount = properties.getBoosThreads();
        int workerThreadCount = properties.getWorkerThreads();
        int rpcPort = properties.getRpcPort();
        log.info("Start to create thrift netty server, boosThreadCount {}, workerThreadCount {}, binding rpc port {}", boosThreadCount, workerThreadCount, rpcPort);

        //netty thread pool.
        ExecutorService boos = Executors.newFixedThreadPool(boosThreadCount, new NamedThreadFactory(EVENT_LOOP_BOSS_POOL_NAME));
        ExecutorService worker = Executors.newFixedThreadPool(workerThreadCount, new NamedThreadFactory(EVENT_LOOP_WORKER_POOL_NAME));
        ExecutorService logicWorker = Executors.newFixedThreadPool(workerThreadCount, new NamedThreadFactory(EVENT_LOOP_WORKER_POOL_NAME));
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

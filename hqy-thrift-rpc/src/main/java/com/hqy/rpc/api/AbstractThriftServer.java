package com.hqy.rpc.api;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.config.ThriftServerProperties;
import com.hqy.rpc.event.ThriftServerStatsEventHandler;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 开启Thrift rpc服务端
 * thrift服务基础类 交给spring管理
 * @author qy
 * @date  2021-08-16 11:05
 */
@Slf4j
@Component
public abstract class AbstractThriftServer implements InitializingBean {


    /**
     * 项目端口
     */
    @Value("${server.port:0}")
    int port = 0;

    /**
     * 当前服务器IP 端口 进程号
     */
    private UsingIpPort uip = null;


    private List<ThriftEventHandler> eventHandlers = new LinkedList<>();

    public void registryEventHandler(List<ThriftEventHandler> eventHandlers) {
        if (CollectionUtils.isNotEmpty(eventHandlers)) {
            this.eventHandlers = eventHandlers;
        }
    }



    @Bean
    @ConditionalOnBean(ThriftServerProperties.class)
    @ConditionalOnMissingBean(ThriftServer.class)
    public ThriftServer createThriftServer(ThriftServerProperties properties) {
        AssertUtil.isTrue(port != 0, "AbstractThriftServer get service port fail, port == 0.");
        boolean registryThriftServer = true;
        List<RPCService> serviceList4Register = getServiceList4Register();
        if (CommonSwitcher.ENABLE_THRIFT_SERVER_BEAN.isOff()) {
            log.warn("@@@ [createThriftServer] CommonSwitcher.ENABLE_THRIFT_SERVER_BEAN = false");
            registryThriftServer = false;
        } else {
            //获取当前服务暴露的rpc接口列表
            if (serviceList4Register == null) {
                //如果是不对外提供rpc服务的独立的节点 则无需注册ThriftServer
                log.info("@@@ FLAG_RPC_REDUCED_SERVICE 标记为无对外提供RPC服务的节点 ");
                EnvironmentConfig.FLAG_RPC_REDUCED_SERVICE = true;
                registryThriftServer = false;
            } else {
                determineProcessingProperties(serviceList4Register.size(), properties);
                log.info("@@@ determineProcessingProperties:{}", JsonUtil.toJson(properties));
            }
        }

        String ip = IpUtil.getHostAddress();
        int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);

        if (registryThriftServer) {
            InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

            List<ThriftEventHandler> eventHandlers = registerThriftEventHandler();
            ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(), eventHandlers, serviceList4Register);

            ExecutorService boosExecutor = new ThreadPoolExecutor(properties.getNettyBossThreadNum(), properties.getNettyBossThreadNum(), 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("BossWorker"));
            ExecutorService ioWorkerExecutor = new ThreadPoolExecutor(properties.getNettyIoWorkerThreadNum(), properties.getNettyIoWorkerThreadNum(), 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("IoWorker"));
            ExecutorService logicWorkerExecutor = new ThreadPoolExecutor(properties.getNettyLogicThreadNum(), properties.getNettyLogicThreadNum(), 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("LogicWorker"));

            final NettyServerConfig serverConfig = NettyServerConfig.newBuilder().setBossThreadExecutor(boosExecutor)
                    .setBossThreadCount(properties.getNettyBossThreadNum()).setWorkerThreadExecutor(ioWorkerExecutor)
                    .setWorkerThreadCount(properties.getNettyIoWorkerThreadNum()).build();

            int rpcPort = getEnableRpcPort(ip, properties);
            uip = new UsingIpPort(ip, port, properties.getRpcPort(), pid);
            log.info("@@@ Registry ThriftServer, uip = {}", JsonUtil.toJson(uip));

            ThriftServerDef serverDef = ThriftServerDef.newBuilder().listen(rpcPort).withProcessor(processor)
                    .using(logicWorkerExecutor).build();
            return new ThriftServer(serverConfig, serverDef);
        } else {
            //表示当前服务没有启动rpc实例 则无需注册ThriftServer 只需注册uip
            uip = new UsingIpPort(ip, port, 0, pid);
            log.info("@@@ This service not registry ThriftServer, uip = {}", JsonUtil.toJson(uip));
            return null;
        }

    }

    private void determineProcessingProperties(int size, ThriftServerProperties properties) {
        int nettyIoWorkerThreadNum = properties.getNettyIoWorkerThreadNum();
        int nettyLogicThreadNum = properties.getNettyLogicThreadNum();
        if (size <= 1) {
            //如果是暴露的RPC实例列表较少时, 可以适当减少io线程数
            log.info("@@@ RPC实例数较低, 适当减少RPC处理线程");
            int boundary = 6;
            nettyIoWorkerThreadNum = nettyIoWorkerThreadNum / boundary;
            if (nettyIoWorkerThreadNum > boundary) {
                nettyIoWorkerThreadNum = boundary;
            }
            nettyLogicThreadNum = nettyLogicThreadNum / boundary;
            if (nettyLogicThreadNum > boundary) {
                nettyIoWorkerThreadNum = boundary;
            }
        } else if (EnvironmentConfig.FLAG_IO_INTENSIVE_RPC_SERVICE) {
            //如果是io密集型的应用...RPC处理线程数再翻倍
            log.info("### IO密集型业务项目，线程数目 再次翻倍！");
            nettyIoWorkerThreadNum = nettyIoWorkerThreadNum * 2;
            nettyLogicThreadNum = nettyLogicThreadNum * 2;
        } else {
            log.info("### 普通rpc-服务准备启动：{} 倍CPU核数的IO线程!!!", properties.getMultipleBaseCount());
        }
        properties.setNettyIoWorkerThreadNum(nettyIoWorkerThreadNum);
        properties.setNettyLogicThreadNum(nettyLogicThreadNum);
    }


    private List<ThriftEventHandler> registerThriftEventHandler() {
        List<ThriftEventHandler> thriftEventHandlers = new LinkedList<>();
        if (CommonSwitcher.ENABLE_THRIFT_RPC_COLLECTION.isOn()) {
            //注册ThriftServerStatsEventHandler 用于rpc采集. 调用链持久化.
            ThriftEventHandler thriftServerStatsEventHandler = new ThriftServerStatsEventHandler();
            thriftEventHandlers.add(thriftServerStatsEventHandler);
        }
        return thriftEventHandlers;
    }



    /**
     * 获取可以使用的rpc端口
     * @param ip
     * @param properties
     * @return
     */
    private int getEnableRpcPort(String ip, ThriftServerProperties properties) {
        int rpcPort = properties.getRpcPort();
        int connectRetryTime = properties.getConnectRetryTime();
        int i = 0;
        while (isPortUsing(rpcPort)) {
            log.warn("### The server port already bind! retry new port!!! [{}:{}]", ip, rpcPort);
            i++;
            if(i == connectRetryTime){
                log.warn("@@@ 重试了{}次，仍然拿不到可用的端口～ 悲剧了！", i);
                throw new RuntimeException("Port is using ! Server failed start after MAX_RETRY_TIMES:" + connectRetryTime);
            }else{
                //端口号使用 涨幅 + 4
                rpcPort = rpcPort + 4;
            }
        }
        return rpcPort;
    }

    /**
     * 判断端口是否被占用
     * @param serverPort 端口是否可用
     * @return boolean result.
     */
    public static boolean isPortUsing(int serverPort) {
        boolean result = true;
        try {
            ServerSocket socket = new ServerSocket(serverPort);
            result = false;
            socket.close();
        } catch (Exception e) {
            //绑定端口失败...
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.warn("@@@ Current port: {} bind failure, retry again.", serverPort);
            }
        }
        return result;
    }


    /**
     * 子类拓展：获取子类提供的RPC服务实例
     * @return List<RPCService>
     */
    public abstract List<RPCService> getServiceList4Register();


    /**
     * 获取当前节点暴露的RPC列表
     * @return RPC接口列表
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends AbstractRPCService>> getRpcServiceClasses() {
        List<RPCService> register = getServiceList4Register();
        if (Objects.isNull(register)) {
            return null;
        }
        List<Class<? extends AbstractRPCService>> rpcList = new ArrayList<>();
        for (RPCService rpcService : register) {
            rpcList.add( (Class<? extends AbstractRPCService>) rpcService.getClass());
        }
        return rpcList;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("afterPropertiesSet ok !");
    }


    public UsingIpPort getUsingIpPort() {
        return uip;
    }
}

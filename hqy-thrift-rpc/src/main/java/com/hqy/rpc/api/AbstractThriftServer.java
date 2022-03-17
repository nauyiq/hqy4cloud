package com.hqy.rpc.api;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    private final int MULTIPLE_BASE = 4;

    private static final int MAX_RETRY_TIMES = 64;

    /**
     * 项目端口
     */
    @Value("${server.port:0}")
    int port = 0;

    /**
     * rpc端口 默认10001
     */
    @Value("${thrift.listen.port:10001}")
    int rpcPort = 10001;


    /**
     * netty boos线程池线程个数 默认1
     */
    @Value("${thrift.acceptor.thread:1}")
    int bossThreadNum = 1;

    protected int ioWorkerThreadNum = Runtime.getRuntime().availableProcessors() * MULTIPLE_BASE;

    protected int logicThreadNum = Runtime.getRuntime().availableProcessors() * MULTIPLE_BASE;

    /**
     * 当前服务器IP 端口 进程号
     */
    private UsingIpPort uip = null;

    @Bean
    public ThriftServer getThriftServer() {

        AssertUtil.isTrue(port != 0, "AbstractThriftServer get service port fail, port == 0.");
        //是否注册ThriftServer
        boolean registryThriftServer = true;
        if (CommonSwitcher.ENABLE_THRIFT_SERVER_BEAN.isOff()) {
            log.warn("### AbstractThriftServer[getThriftServer] CommonSwitcher ENABLE_THRIFT_SERVER_BEAN = false");
            log.warn("### Can Not Get ThriftServer Bean.");
            registryThriftServer = false;
        } else {
            //获取当前服务暴露的rpc接口列表
            List<Class<? extends AbstractRPCService>> rpcServiceClasses = getRpcServiceClasses();
            if (rpcServiceClasses == null) {
                //如果是不对外提供rpc服务的独立的节点 则无需注册ThriftServer
                log.info("### FLAG_RPC_REDUCED_SERVICE 标记为无对外提供RPC服务的节点 ");
                EnvironmentConfig.FLAG_RPC_REDUCED_SERVICE = true;
                registryThriftServer = false;
            } else if (rpcServiceClasses.size() <= 1) {
                //如果是暴露的RPC实例列表较少时, 可以适当减少io线程数
                log.info("### RPC实例数较低, 适当减少RPC处理线程");
                int boundary = 6;
                ioWorkerThreadNum = ioWorkerThreadNum / MULTIPLE_BASE;
                if (ioWorkerThreadNum > boundary) {
                    ioWorkerThreadNum = boundary;
                }
                logicThreadNum = logicThreadNum / MULTIPLE_BASE;
                if (logicThreadNum > boundary) {
                    logicThreadNum = boundary;
                }
            } else if (EnvironmentConfig.FLAG_IO_INTENSIVE_RPC_SERVICE) {
                //如果是io密集型的应用...RPC处理线程数再翻倍
                ioWorkerThreadNum = ioWorkerThreadNum * 2;
                logicThreadNum = logicThreadNum * 2;
                log.info("### EnvironmentConfig.FLAG_IO_INTENSIVE_RPC_SERVICE!");
                log.info("### IO密集型业务项目，线程数目 再次翻倍！");
            } else {
                log.info("### 普通rpc-服务准备启动：{} 倍CPU核数的IO线程!!!", MULTIPLE_BASE);
            }
            log.info("### 注册rpc实例：{}", JsonUtil.toJson(rpcServiceClasses));
            log.info("AbstractThriftServer[getThriftServer]  bossThreadNum:{}, ioThreadNum:{}, logicThreadNum:{}",
                    bossThreadNum, ioWorkerThreadNum, logicThreadNum);
            log.info("AbstractThriftServer[getThriftServer]  default configured serverPort:{}", rpcPort);
        }

        String ip = IpUtil.getHostAddress();
        int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        if (registryThriftServer) {
            InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

            //TODO 设置服务端事件处理器 ThriftEventHandler
            List<ThriftEventHandler> eventHandlers = new LinkedList<>();
            List<RPCService> rpcServices = getServiceList4Register();
            ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(), eventHandlers, rpcServices);

            ExecutorService boosExecutor = new ThreadPoolExecutor(this.bossThreadNum, this.bossThreadNum, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("BossWorker"));
            ExecutorService ioWorkerExecutor = new ThreadPoolExecutor(this.ioWorkerThreadNum, this.ioWorkerThreadNum, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("IoWorker"));
            ExecutorService logicWorkerExecutor = new ThreadPoolExecutor(this.logicThreadNum, this.logicThreadNum, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory("LogicWorker"));

            final NettyServerConfig serverConfig = NettyServerConfig.newBuilder().setBossThreadExecutor(boosExecutor)
                    .setBossThreadCount(this.bossThreadNum).setWorkerThreadExecutor(ioWorkerExecutor)
                    .setWorkerThreadCount(this.ioWorkerThreadNum).build();

            //获取可以使用的端口.
            getRpcPort(ip);
            uip = new UsingIpPort(ip, port, rpcPort, pid);
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

    private void getRpcPort(String ip) {
        int i = 0;
        while (isPortUsing(this.rpcPort)) {
            log.warn("### The server port already bind! retry new port!!! [{}:{}]", ip, rpcPort);
            i++;
            if(i == MAX_RETRY_TIMES){
                log.warn("@@@ 重试了{}次，仍然拿不到可用的端口～ 悲剧了！", i);
                throw new RuntimeException("Port is using ! Server failed start after MAX_RETRY_TIMES:" + MAX_RETRY_TIMES);
            }else{
                //端口号使用 涨幅 + 4
                this.rpcPort = this.rpcPort + 4;
            }
        }
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
     * @return
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

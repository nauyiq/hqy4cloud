package com.hqy.rpc.api;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.service.ThriftServer;
import com.hqy.fundation.common.rpc.api.RpcService;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * thrift服务基础类 交给spring管理
 * @author qy
 * @date  2021-08-16 11:05
 */
@Slf4j
@RefreshScope
@Configuration
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

        AssertUtil.isTrue(port == 0, "AbstractThriftServer get service port fail, port == 0.");

        if (CommonSwitcher.ENABLE_THRIFT_SERVER_BEAN.isOff()) {
            log.warn("### AbstractThriftServer[getThriftServer] CommonSwitcher ENABLE_THRIFT_SERVER_BEAN = false");
            log.warn("### Can Not Get ThriftServer Bean.");
            return null;
        } else {
            List<Class<? extends AbstractRpcService>> rpcServiceClasses = getRpcServiceClasses();
            if (CollectionUtils.isEmpty(rpcServiceClasses)) {
                //如果是不对外提供rpc服务的独立的节点 则无需注册ThriftServer
                log.info("### FLAG_RPC_REDUCED_SERVICE 标记为无对外提供RPC服务的节点 ");
                EnvironmentConfig.FLAG_RPC_REDUCED_SERVICE = true;
                return null;
            } else if (rpcServiceClasses.size() == 1) {
                //如果是暴露的RPC实例列表较少时, 可以适当减少io线程数
                log.info("### RPC实例数较低, 适当减少RPC处理线程");
                ioWorkerThreadNum = ioWorkerThreadNum / MULTIPLE_BASE;
                if (ioWorkerThreadNum > 6) {
                    ioWorkerThreadNum = 6;
                }
                logicThreadNum = logicThreadNum / MULTIPLE_BASE;
                if (logicThreadNum > 6) {
                    logicThreadNum = 6;
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

            for (Class<? extends AbstractRpcService> rpcServiceClass : rpcServiceClasses) {
                log.info("### 注册rpc实例：{}", rpcServiceClass);
            }
            log.info("AbstractThriftServer[getThriftServer]  bossThreadNum:{}, ioThreadNum:{}, logicThreadNum:{}",
                    bossThreadNum, ioWorkerThreadNum, logicThreadNum);
            log.info("AbstractThriftServer[getThriftServer]  default configured serverPort:{}", rpcPort);

            InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

            ExecutorService boosExecutor = Executors.newFixedThreadPool(this.bossThreadNum, new DefaultThreadFactory("BossWorker"));
            ExecutorService ioWorkerExecutor = Executors.newFixedThreadPool(this.ioWorkerThreadNum, new DefaultThreadFactory("IoWorker"));
            ExecutorService logicWorkerExecutor = Executors.newFixedThreadPool(this.ioWorkerThreadNum, new DefaultThreadFactory("LogicWorker"));

            final NettyServerConfig serverConfig = NettyServerConfig.newBuilder().setBossThreadExecutor(boosExecutor)
                    .setBossThreadCount(this.bossThreadNum).setWorkerThreadExecutor(ioWorkerExecutor)
                    .setWorkerThreadCount(this.ioWorkerThreadNum).build();

            String ip = IpUtil.getHostAddress();
            int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            log.info("### before starting: ip -> {}, pid -> {}", ip, pid);

            int tryTime = 0;
            while (isPortUsing(this.rpcPort)) {
                log.warn("### The server port already bind! retry new port!!! [{}:{}]", ip, rpcPort);
                tryTime ++;
                if(tryTime == MAX_RETRY_TIMES){
                    System.err.println("重试了MAX_RETRY_TIMES次，仍然拿不到可用的端口～ 悲剧了！");
                    throw new RuntimeException("Port is using ! Server failed start after MAX_RETRY_TIMES:" + MAX_RETRY_TIMES);
                }else{
                    //端口号使用 涨幅 + 4
                    this.rpcPort = this.rpcPort + 4;
                }
            }
            ThriftServerDef serverDef = ThriftServerDef.newBuilder().listen(rpcPort).using(logicWorkerExecutor).build();

            uip = new UsingIpPort(ip, port, rpcPort, pid);
            return new ThriftServer(serverConfig, serverDef);
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
    public abstract List<RpcService> getServiceList4Register();


    /**
     * 获取当前节点暴露的RPC列表
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends AbstractRpcService>> getRpcServiceClasses() {
        List<Class<? extends AbstractRpcService>> rpcList = new ArrayList<>();
        List<RpcService> register = getServiceList4Register();
        if (CollectionUtils.isEmpty(register)) {
            return rpcList;
        }
        for (RpcService rpcService : register) {
            rpcList.add( (Class<? extends AbstractRpcService>) rpcService.getClass());
        }
        return rpcList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("afterPropertiesSet ok !");
    }

    public UsingIpPort getUsingIpPort() {
        return uip;
    }
}

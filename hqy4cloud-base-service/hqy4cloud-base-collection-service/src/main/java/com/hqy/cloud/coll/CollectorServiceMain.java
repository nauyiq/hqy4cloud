package com.hqy.cloud.coll;

import com.hqy.cloud.coll.service.CollPersistService;
import com.hqy.cloud.coll.service.ExceptionCollectionService;
import com.hqy.cloud.coll.service.RemoteSqlLogCollectionService;
import com.hqy.cloud.coll.service.RpcLogRemoteService;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.monitor.thrift.service.ThriftMonitorService;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Arrays;
import java.util.List;

/**
 * 提供各个模块的采集服务 <br>
 * 或对外暴露RPC服务 接收各模块的数据上报...
 * @author qy
 * @date 2021/8/19 22:13
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.hqy.cloud.**.mapper"})
public class CollectorServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(CollectorServiceMain.class, args);
        ProjectContextInfo.startPrintf();
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {
        private final CollPersistService collPersistService;
        private final ThriftMonitorService thriftMonitorService;
        private final ExceptionCollectionService exceptionCollectionService;
        private final RpcLogRemoteService rpcLogRemoteService;
        private final RemoteSqlLogCollectionService sqlLogCollectionService;

        @Override
        public List<RPCService> getRpcServices() {
            return List.of(collPersistService, thriftMonitorService, exceptionCollectionService, rpcLogRemoteService, sqlLogCollectionService);
        }
    }


}

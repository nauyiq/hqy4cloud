package com.hqy.cloud.thrift.core;

import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.thrift.server.support.SeataGlobalTransactionEventHandler;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 17:30
 */
public class ThriftServerModel {

    private final List<RPCService> thriftRpcServices;

    private final List<ThriftServerContextHandleService> serverThriftEventHandlers = new ArrayList<>();

    private final ThriftServerProperties thriftServerProperties;

    public ThriftServerModel(List<RPCService> thriftRpcServices, List<ThriftServerContextHandleService> serverThriftEventHandlers, ThriftServerProperties thriftServerProperties) {
        this.thriftRpcServices = thriftRpcServices;
        this.thriftServerProperties = thriftServerProperties;
        this.serverThriftEventHandlers.add(new SeataGlobalTransactionEventHandler());
        if (CollectionUtils.isNotEmpty(serverThriftEventHandlers)) {
            this.serverThriftEventHandlers.addAll(serverThriftEventHandlers);
        }
    }
    public List<ThriftServerContextHandleService> getServerThriftEventHandlers() {
        return serverThriftEventHandlers;
    }

    public List<RPCService> getThriftRpcServices() {
        return thriftRpcServices;
    }

    public ThriftServerProperties getThriftServerProperties() {
        return thriftServerProperties;
    }
}

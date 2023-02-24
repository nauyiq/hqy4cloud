package com.hqy.rpc.thrift.handler.support;

import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.handler.server.support.SeataGlobalTransactionEventHandler;
import com.hqy.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.rpc.thrift.support.ThriftServerProperties;
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

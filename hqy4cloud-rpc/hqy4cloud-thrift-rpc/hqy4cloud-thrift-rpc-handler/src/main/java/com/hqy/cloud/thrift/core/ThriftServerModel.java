package com.hqy.cloud.thrift.core;

import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 17:30
 */
public class ThriftServerModel {

    private final List<RPCService> thriftRpcServices;
    private final ThriftServerProperties thriftServerProperties;
    private final List<ThriftServerContextHandleService> thriftServerContextHandleServices;

    public ThriftServerModel(List<RPCService> thriftRpcServices, List<ThriftServerContextHandleService> thriftServerContextHandleServices, ThriftServerProperties thriftServerProperties) {
        this.thriftRpcServices = thriftRpcServices;
        this.thriftServerProperties = thriftServerProperties;
        this.thriftServerContextHandleServices = thriftServerContextHandleServices;
    }
    public List<ThriftServerContextHandleService> getServerThriftEventHandlers() {
        return thriftServerContextHandleServices;
    }

    public List<RPCService> getThriftRpcServices() {
        return thriftRpcServices;
    }

    public ThriftServerProperties getThriftServerProperties() {
        return thriftServerProperties;
    }
}

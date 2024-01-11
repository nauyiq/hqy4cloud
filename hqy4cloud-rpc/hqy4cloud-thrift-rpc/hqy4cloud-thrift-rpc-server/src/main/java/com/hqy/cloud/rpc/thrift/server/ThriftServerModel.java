package com.hqy.cloud.rpc.thrift.server;

import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerProperties;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7
 */
public class ThriftServerModel {

    private final RpcModel rpcModel;
    private List<RPCService> thriftRpcServices;
    private final ThriftServerProperties thriftServerProperties;
    private List<ThriftServerContextHandleService> thriftServerContextHandleServices;

    public ThriftServerModel(RpcModel rpcModel, ThriftServerProperties thriftServerProperties) {
        this.rpcModel = rpcModel;
        this.thriftServerProperties = thriftServerProperties;
    }

    public ThriftServerModel(RpcModel rpcModel, List<RPCService> thriftRpcServices, List<ThriftServerContextHandleService> thriftServerContextHandleServices, ThriftServerProperties thriftServerProperties) {
        this.rpcModel = rpcModel;
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

    public RpcModel getRpcModel() {
        return rpcModel;
    }


    public void setThriftRpcServices(List<RPCService> thriftRpcServices) {
        this.thriftRpcServices = thriftRpcServices;
    }

    public void setThriftServerContextHandleServices(List<ThriftServerContextHandleService> thriftServerContextHandleServices) {
        this.thriftServerContextHandleServices = thriftServerContextHandleServices;
    }
}

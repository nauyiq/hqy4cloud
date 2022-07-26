package com.hqy.rpc.monitor.thrift.api;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.struct.ThriftRpcExceptionStruct;
import com.hqy.rpc.thrift.struct.ThriftRpcFlowStruct;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 14:32
 */
@ThriftService
public interface ThriftMonitorService extends RPCService {

    /**
     * Collects RPC flow at intervals.
     * @param struct rpc interval flow detail. {@link ThriftRpcFlowStruct}
     */
    @ThriftMethod(oneway = true)
    void collectRpcFlow(@ThriftField(1) ThriftRpcFlowStruct struct);

    /**
     * Collects RPC flow at intervals.
     * @param structs rpc interval flow detail. {@link ThriftRpcFlowStruct}
     */
    @ThriftMethod(oneway = true)
    void collectRpcFlowList(@ThriftField(1)List<ThriftRpcFlowStruct> structs);

    /**
     * Collects Rpc exception.
     * slow or exception.
     * @param struct rpc exception detail. {@link ThriftRpcExceptionStruct}
     */
    @ThriftMethod(oneway = true)
    void collectRpcException(@ThriftField(1)ThriftRpcExceptionStruct struct);

}

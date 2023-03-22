package com.hqy.cloud.coll.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.coll.struct.PageRpcExceptionRecordStruct;
import com.hqy.cloud.coll.struct.PageRpcFlowRecordStruct;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:25
 */
@ThriftService(MicroServiceConstants.COMMON_COLLECTOR)
public interface RpcLogRemoteService extends RPCService {


    /**
     * 分页查询rpc流量日志
     * @param caller     RPC调用者
     * @param provider   RPC提供者
     * @param pageStruct 分页请求参数
     * @return           {@link PageRpcFlowRecordStruct}
     */
    @ThriftMethod
    PageRpcFlowRecordStruct pageRpcFlowLog(@ThriftField(1) String caller, @ThriftField(2) String provider, @ThriftField(3) PageStruct pageStruct);

    /**
     * 删除rpc流量日志记录
     * @param id id.
     */
    @ThriftMethod(oneway = true)
    void deleteRpcFlowLogRecord(@ThriftField(1) Long id);

    /**
     * 分页查询rpc错误日志
     * @param application       服务名
     * @param serviceClassName  rpc类名
     * @param type              类型
     * @param struct            分页请求参数
     * @return                  {@link PageRpcExceptionRecordStruct}
     */
    @ThriftMethod
    PageRpcExceptionRecordStruct pageRpcErrorLog(@ThriftField(1) String application, @ThriftField(2) String serviceClassName, @ThriftField(3) Integer type, @ThriftField(4) PageStruct struct);

    /**
     * 删除rpc错误日志记录
     * @param id id.
     */
    @ThriftMethod(oneway = true)
    void deleteRpcExceptionRecord(@ThriftField(1) Long id);

}

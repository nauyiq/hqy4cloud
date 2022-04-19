package com.hqy.coll.service;


import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.coll.struct.RPCMinuteFlowRecordStruct;
import com.hqy.coll.struct.ThrottledIpBlockStruct;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

/**
 * 采集服务RPC接口
 * @author qy
 * @date 2021-08-10 15:23
 */
@ThriftService(MicroServiceConstants.COMMON_COLLECTOR)
public interface CollPersistService extends RPCService {

    /**
     * 保存一条网关封禁的ip记录到数据库
     * @param struct thrift rpc struct
     */
    @ThriftMethod(oneway = true)
    void saveThrottledIpBlockHistory(@ThriftField(1) ThrottledIpBlockStruct struct);

    /**
     * 保存rpc分钟窗口流量记录入库
     * @param struct
     */
    @ThriftMethod(oneway = true)
    void saveRpcMinuteFlowRecord(@ThriftField(1) RPCMinuteFlowRecordStruct struct);
}

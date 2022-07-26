package com.hqy.coll.service;


import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.coll.struct.ThrottledIpBlockStruct;
import com.hqy.rpc.api.service.RPCService;

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

}

package com.hqy.cloud.coll.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.service.RPCService;

/**
 * 采集sql记录 - 远程调用service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 9:41
 */
@ThriftService(MicroServiceConstants.COMMON_COLLECTOR)
public interface RemoteSqlLogCollectionService extends RPCService {

    /**
     * 添加一条sql记录
     * @param struct {@link SqlRecordStruct}
     */
    @ThriftMethod(oneway = true)
    void addSqlRecord(@ThriftField(1) SqlRecordStruct struct);


}

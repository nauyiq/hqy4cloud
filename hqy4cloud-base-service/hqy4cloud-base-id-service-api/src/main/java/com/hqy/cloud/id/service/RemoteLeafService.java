package com.hqy.cloud.id.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.id.struct.ResultStruct;
import com.hqy.rpc.api.service.RPCService;

/**
 * leaf-segment rpc service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 14:44
 */
@ThriftService(value = MicroServiceConstants.ID_SERVICE)
public interface RemoteLeafService extends RPCService {


    /**
     * 获取segment id
     * @param key 业务key.
     * @return    {@link ResultStruct}
     */
    @ThriftMethod
    ResultStruct getSegmentId(@ThriftField(1) String key);


}

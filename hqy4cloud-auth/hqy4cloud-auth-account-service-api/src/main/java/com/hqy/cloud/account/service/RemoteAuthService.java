package com.hqy.cloud.account.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 13:48
 */
@ThriftService(MicroServiceConstants.ACCOUNT_SERVICE)
public interface RemoteAuthService extends RPCService {

    /**
     * basic认证
     * @param clientId     租户id
     * @param clientSecret 租户秘钥
     * @return             result {@link CommonResultStruct}
     */
    @ThriftMethod
    CommonResultStruct basicAuth(@ThriftField(1) String clientId, @ThriftField(2) String clientSecret);


}

package com.hqy.account.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.account.struct.AccountProfileStruct;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.service.RPCService;

/**
 * RemoteAccountProfileService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:11
 */
@ThriftService(value = MicroServiceConstants.ACCOUNT_SERVICE)
public interface RemoteAccountProfileService extends RPCService {

    /**
     * uploadAccountProfile.
     * @param profileStruct profile.
     * @return              update result.
     */
    @ThriftMethod
    boolean uploadAccountProfile(@ThriftField(1)AccountProfileStruct profileStruct);


}

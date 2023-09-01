package com.hqy.cloud.account.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
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

    /**
     * return user account info and profile info.
     * @param userId user id.
     * @return       {@link AccountProfileStruct}
     */
    @ThriftMethod
    AccountProfileStruct getAccountProfile(@ThriftField(1) Long userId);


    /**
     * return user account info and profile info.
     * @param usernameOrEmail  username or email.
     * @return                 {@link AccountProfileStruct}
     */
    @ThriftMethod
    AccountProfileStruct getAccountProfile(@ThriftField(1) String usernameOrEmail);

}

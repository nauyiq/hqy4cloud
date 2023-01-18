package com.hqy.account.service.remote;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.account.struct.AccountProfileStruct;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.api.service.RPCService;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:11
 */
@ThriftService(value = MicroServiceConstants.ACCOUNT_SERVICE)
public interface AccountProfileRemoteService extends RPCService {

    /**
     * uploadAccountProfile.
     * @param profileStruct profile.
     * @return              update result.
     */
    @ThriftMethod
    boolean uploadAccountProfile(@ThriftField(1)AccountProfileStruct profileStruct);

    /**
     * 获取用户profile信息
     * @param ids 用户ids.
     * @return    AccountProfileStruct.
     */
    @ThriftMethod
    List<AccountProfileStruct> getAccountProfiles(@ThriftField(1) List<Long> ids);


}

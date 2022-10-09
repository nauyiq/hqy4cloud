package com.hqy.account.service.remote;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.api.service.RPCService;

import java.util.List;
import java.util.Set;

/**
 * thrift rpc for account
 * @author qiyuan.hong
 * @date 2022-03-16 11:17
 */
@ThriftService(MicroServiceConstants.ACCOUNT_SERVICE)
public interface AccountRemoteService extends RPCService {

    /**
     * get account json info.
     * @param id  account id.
     * @return    account json info.
     */
    @ThriftMethod
    String getAccountInfoJson(@ThriftField(1) Long id);


    /**
     * 获取用户基本信息
     * @param id 查找哪个用户的基本信息
     * @return   AccountBaseInfoStruct.
     */
    @ThriftMethod
    AccountBaseInfoStruct getAccountBaseInfo(@ThriftField(1)Long id);

    /**
     * 获取用户基本信息
     * @param ids 查找哪些用户的基本信息
     * @return    AccountBaseInfoStruct.
     */
    @ThriftMethod
    List<AccountBaseInfoStruct> getAccountBaseInfos(@ThriftField(1)List<Long> ids);
}

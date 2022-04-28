package com.hqy.order.common.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.rpc.transaction.GlobalTransactionalThriftMethod;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:40
 */
@ThriftService(MicroServiceConstants.TRANSACTION_ACCOUNT_SERVICE)
public interface AccountRemoteService extends RPCService {

    /**
     * 修改account
     * @param account account json
     * @return 是否修改成功
     */
    @ThriftMethod
    @GlobalTransactionalThriftMethod
    boolean modifyAccount(@ThriftField(1)String account);


    @ThriftMethod
    String getAccountById(@ThriftField(1) Long account);
}

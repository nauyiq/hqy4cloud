package com.hqy.order.common.service;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

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
    boolean modifyAccount(String account);
}

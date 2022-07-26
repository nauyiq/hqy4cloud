package com.hqy.account.service.remote;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.api.service.RPCService;

/**
 * thrift rpc for account
 * @author qiyuan.hong
 * @date 2022-03-16 11:17
 */
@ThriftService(MicroServiceConstants.ACCOUNT_SERVICE)
public interface AccountRemoteService extends RPCService {
}

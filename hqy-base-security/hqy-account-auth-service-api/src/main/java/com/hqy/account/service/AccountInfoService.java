package com.hqy.account.service;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 11:17
 */
@ThriftService(MicroServiceConstants.ACCOUNT_SERVICE)
public interface AccountInfoService extends RPCService {
}

package com.hqy.order.common.service;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:16
 */
@ThriftService(MicroServiceConstants.TRANSACTION_STORAGE_SERVICE)
public interface StorageRemoteService extends RPCService {
}

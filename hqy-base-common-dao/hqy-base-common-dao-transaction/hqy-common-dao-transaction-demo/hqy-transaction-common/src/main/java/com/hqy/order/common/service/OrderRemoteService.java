package com.hqy.order.common.service;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 11:09
 */
@ThriftService(value = MicroServiceConstants.TRANSACTION_ORDER_SERVICE)
public interface OrderRemoteService extends RPCService {



}

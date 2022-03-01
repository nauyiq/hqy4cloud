package com.hqy.gateway.service;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.fundation.common.base.project.MicroServiceConstants;
import com.hqy.fundation.common.rpc.api.RPCService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 11:06
 */
@ThriftService(MicroServiceConstants.GATEWAY)
public interface GateWayService extends RPCService {


    @ThriftMethod
    void test();
}

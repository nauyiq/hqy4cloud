package com.hqy.coll.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.coll.struct.PfExceptionStruct;
import com.hqy.foundation.common.enums.ExceptionLevel;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.foundation.common.enums.ExceptionType;
import com.hqy.rpc.api.service.RPCService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 17:02
 */
@ThriftService(MicroServiceConstants.COMMON_COLLECTOR)
public interface ExceptionCollectionService extends RPCService {


    /**
     * 异常采集
     * @param struct {@link PfExceptionStruct}
     */
    @ThriftMethod(oneway = true)
    void collect(@ThriftField(1)PfExceptionStruct struct);
}

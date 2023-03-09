package com.hqy.cloud.coll.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.coll.struct.PageExceptionLogStruct;
import com.hqy.cloud.coll.struct.PfExceptionStruct;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.struct.PageStruct;

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

    /**
     * 分页查询错误日志
     * @param serviceName    服务名
     * @param type           错误类型
     * @param env            环境
     * @param exceptionClass 异常类
     * @param ip             ip
     * @param url            url
     * @param struct         分页参数
     * @return               {@link PageExceptionLogStruct}
     */
    @ThriftMethod
    PageExceptionLogStruct queryPage(@ThriftField(1) String serviceName, @ThriftField(2) String type,
                                     @ThriftField(3) String env, @ThriftField(4) String exceptionClass,
                                     @ThriftField(5) String ip, @ThriftField(6) String url, @ThriftField(7) PageStruct struct);


    /**
     * 删除错误日志
     * @param id id
     */
    @ThriftMethod(oneway = true)
    void deleteErrorLog(@ThriftField(1) Long id);

}

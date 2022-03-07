package com.hqy.coll.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.fundation.common.base.project.MicroServiceConstants;
import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.fundation.enums.ExceptionLevel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 17:02
 */
@ThriftService(MicroServiceConstants.COMMON_COLLECTOR)
public interface ExceptionCollectionService extends RPCService {


    /**
     * 异常采集.
     * @param time 异常时间
     * @param exceptionClass 异常类信息 （class.getSimpleName）
     * @param stackTrace 异常栈信息
     * @param resultCode 状态码(推荐使用CommonResultCode的code定义，也可以是业务场景的定义)
     * @param env 当前环境
     * @param nameEn 发现异常的节点的英文名称 ;module 名称；
     * @param level 异常级别
     * @param param 其他需要提供的辅助的信息，建议使用json 支持多个属性的存储
     */
    @ThriftMethod
    void collect(@ThriftField(1) long time, @ThriftField(2)String exceptionClass, @ThriftField(3)String stackTrace,
                 @ThriftField(4)int resultCode, @ThriftField(5)String env,
                 @ThriftField(6)String nameEn, @ThriftField(7)ExceptionLevel level, @ThriftField(8)String param);
}

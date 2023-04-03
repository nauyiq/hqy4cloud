package com.hqy.cloud.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 14:21
 */
@Builder
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class PfExceptionStruct {

    /**
     * id
     */
    @ThriftField(1)
    public Long id;

    /**
     * 服务名
     */
    @ThriftField(2)
    public String serviceName;

    /**
     * 异常类型
     */
    @ThriftField(3)
    public String type;

    /**
     * 环境
     */
    @ThriftField(4)
    public String environment;

    /**
     * 异常类
     */
    @ThriftField(5)
    public String exceptionClass;

    /**
     * 异常堆栈
     */
    @ThriftField(6)
    public String stackTrace;

    /**
     * 错误的业务状态码
     */
    @ThriftField(7)
    public Integer resultCode;

    /**
     * url 针对web请求出错的才会采集
     */
    @ThriftField(8)
    public String url;

    /**
     * ip 针对web请求出错的才会采集
     */
    @ThriftField(9)
    public String ip;

    /**
     * 其他需要提供的辅助的信息，建议使用json 支持多个属性的存储
     */
    @ThriftField(10)
    public String params;

    /**
     * 创建时间
     */
    @ThriftField(11)
    public String created;


}

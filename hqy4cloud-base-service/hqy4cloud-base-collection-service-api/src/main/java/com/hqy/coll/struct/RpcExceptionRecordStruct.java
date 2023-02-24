package com.hqy.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:35
 */
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class RpcExceptionRecordStruct {

    @ThriftField(1)
    public Long id;

    /**
     * rpc类型 normal/slow/error
     */
    @ThriftField(2)
    public String type;

    /**
     * provider application name.
     */
    @ThriftField(3)
    public String application;

    /**
     * rpc接口名
     */
    @ThriftField(4)
    public String serviceClassName;

    /**
     * rpc方法
     */
    @ThriftField(5)
    public String method;

    /**
     * 请求时间戳
     */
    @ThriftField(6)
    public String requestTime;

    /**
     * 耗时
     */
    @ThriftField(7)
    public Long elapsed;

    /**
     * 错误消息
     */
    @ThriftField(8)
    public String message;

}

package com.hqy.rpc.thrift.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 15:37
 */
@ThriftStruct
public final class ThriftRpcExceptionStruct {

    /**
     * exception type.
     */
    @ThriftField(1)
    public String type;

    /**
     * request timestamp.
     */
    @ThriftField(2)
    public long requestTime;

    /**
     * provider application name.
     */
    @ThriftField(3)
    public String application;

    /**
     * rpc interface name.
     */
    @ThriftField(4)
    public String serviceClassName;

    /**
     * method name.
     */
    @ThriftField(5)
    public String method;

    /**
     * elapsed timestamp.
     */
    @ThriftField(6)
    public long elapsed;

    /**
     * exception message.
     */
    @ThriftField(7)
    public String message;


    public ThriftRpcExceptionStruct() {
    }

    public ThriftRpcExceptionStruct(String type, long requestTime, String application, String serviceClassName, String method, long elapsed, String message) {
        this.type = type;
        this.requestTime = requestTime;
        this.application = application;
        this.serviceClassName = serviceClassName;
        this.method = method;
        this.elapsed = elapsed;
        this.message = message;
    }

}

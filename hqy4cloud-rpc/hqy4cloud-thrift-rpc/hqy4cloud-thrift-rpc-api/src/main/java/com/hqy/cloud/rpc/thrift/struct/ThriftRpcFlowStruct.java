package com.hqy.cloud.rpc.thrift.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 14:46
 */
@ThriftStruct
public final class ThriftRpcFlowStruct {

    /**
     * rpc client caller name.
     */
    @ThriftField(1)
    public String caller;

    /**
     * rpc provider name.
     */
    @ThriftField(2)
    public String provider;

    /**
     * success count.
     */
    @ThriftField(3)
    public int success;

    /**
     * failure count.
     */
    @ThriftField(4)
    public int failure;

    /**
     * Total number of calls in the interval.
     */
    @ThriftField(5)
    public int total;

    /**
     * Sampling interval.
     */
    @ThriftField(6)
    public long interval;

    /**
     * remoting rpc interface detail.
     * data format -> json
     */
    @ThriftField(7)
    public String serviceDetail;

    /**
     * remoting rpc method detail.
     * data format -> json
     */
    @ThriftField(8)
    public String methodDetail;


    public ThriftRpcFlowStruct() {
    }

    public ThriftRpcFlowStruct(String caller, String provider, int success, int failure, int total, long interval, String serviceDetail, String methodDetail) {
        this.caller = caller;
        this.provider = provider;
        this.success = success;
        this.failure = failure;
        this.total = total;
        this.interval = interval;
        this.serviceDetail = serviceDetail;
        this.methodDetail = methodDetail;
    }

}

package com.hqy.coll.struct;

import com.facebook.swift.codec.ThriftStruct;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 21:31
 */
@ThriftStruct
public final class RPCMinuteFlowRecordStruct {

    /**
     * 调用者
     */
    public String caller;

    /**
     * 调用的总次数
     */
    public Integer total;

    /**
     * rpc方法调用失败的次数
     */
    public Integer ngTotal;

    /**
     * 当前的时间窗口 格式到分 即hh-MM-dd HH:mm
     */
    public String timeWindow;

    /**
     * 接口分组的计数map
     */
    public String serviceMapJson;

    /**
     * 方法分组的计数map
     */
    public String methodMapJson;

    public RPCMinuteFlowRecordStruct() {
    }

    public RPCMinuteFlowRecordStruct(String caller, Integer total, Integer ngTotal, String timeWindow, String serviceMapJson, String methodMapJson) {
        this.caller = caller;
        this.total = total;
        this.ngTotal = ngTotal;
        this.timeWindow = timeWindow;
        this.serviceMapJson = serviceMapJson;
        this.methodMapJson = methodMapJson;
    }
}

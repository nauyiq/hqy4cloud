package com.hqy.coll.entity;

import com.hqy.base.BaseEntity;
import com.hqy.coll.struct.RPCMinuteFlowRecordStruct;

import javax.persistence.Table;
import java.util.Date;

/**
 * 表t_rpc_minute_flow_record
 * @author qiyuan.hong
 * @date 2022-03-17 21:20
 */
@Table(name = "t_rpc_minute_flow_record")
public class RPCMinuteFlowRecord extends BaseEntity<Long> {

    /**
     * 调用者
     */
    private String caller;

    /**
     * 调用的总次数
     */
    private Integer total;

    /**
     * rpc方法调用失败的次数
     */
    private Integer ngTotal;

    /**
     * 当前的时间窗口 格式到分 即hh-MM-dd HH:mm
     */
    private String timeWindow;

    /**
     * 接口分组的计数map
     */
    private String serviceMapJson;

    /**
     * 方法分组的计数map
     */
    private String methodMapJson;

    public RPCMinuteFlowRecord() {
    }

    public RPCMinuteFlowRecord(RPCMinuteFlowRecordStruct struct) {
        this.caller = struct.caller;
        this.total = struct.total;
        this.ngTotal = struct.ngTotal;
        this.timeWindow = struct.timeWindow;
        this.serviceMapJson = struct.serviceMapJson;
        this.methodMapJson = struct.methodMapJson;
        Date now = new Date();
        super.setCreated(now);
        super.setUpdated(now);
    }

    public RPCMinuteFlowRecord(String caller, Integer total, Integer ngTotal, String timeWindow, String serviceMapJson, String methodMapJson) {
        this.caller = caller;
        this.total = total;
        this.ngTotal = ngTotal;
        this.timeWindow = timeWindow;
        this.serviceMapJson = serviceMapJson;
        this.methodMapJson = methodMapJson;
        Date now = new Date();
        super.setCreated(now);
        super.setUpdated(now);
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getNgTotal() {
        return ngTotal;
    }

    public void setNgTotal(Integer ngTotal) {
        this.ngTotal = ngTotal;
    }

    public String getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(String timeWindow) {
        this.timeWindow = timeWindow;
    }

    public String getServiceMapJson() {
        return serviceMapJson;
    }

    public void setServiceMapJson(String serviceMapJson) {
        this.serviceMapJson = serviceMapJson;
    }

    public String getMethodMapJson() {
        return methodMapJson;
    }

    public void setMethodMapJson(String methodMapJson) {
        this.methodMapJson = methodMapJson;
    }
}

package com.hqy.collector.entity;

import com.hqy.base.BaseEntity;
import com.hqy.rpc.thrift.struct.ThriftRpcFlowStruct;

import javax.persistence.Table;

/**
 * 表t_rpc_minute_flow_record
 * @author qiyuan.hong
 * @date 2022-03-17 21:20
 */
@Table(name = "t_rpc_flow_record")
public class RPCFlowRecord extends BaseEntity<Long> {
    private transient static final long serialVersionUID = -3302078226615090714L;

    /**
     * 调用者
     */
    private String caller;

    /**
     * rpc server.
     */
    private String provider;

    /**
     * 调用的总次数
     */
    private Integer total;

    /**
     * success count.
     */
    private Integer success;

    /**
     * failed count.
     */
    private Integer failure;

    /**
     * collection interval.
     */
    private long interval;

    /**
     * 接口分组的计数map
     */
    private String serviceDetail;

    /**
     * 方法分组的计数map
     */
    private String methodDetail;

    public RPCFlowRecord() {
    }

    public RPCFlowRecord(ThriftRpcFlowStruct struct) {
        this.caller = struct.caller;
        this.provider = struct.provider;
        this.success = struct.success;
        this.failure = struct.failure;
        this.interval = struct.interval;
        this.methodDetail = struct.methodDetail;
        this.serviceDetail = struct.serviceDetail;
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

    public Integer getFailure() {
        return failure;
    }

    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getServiceDetail() {
        return serviceDetail;
    }

    public void setServiceDetail(String serviceDetail) {
        this.serviceDetail = serviceDetail;
    }

    public String getMethodDetail() {
        return methodDetail;
    }

    public void setMethodDetail(String methodDetail) {
        this.methodDetail = methodDetail;
    }
}

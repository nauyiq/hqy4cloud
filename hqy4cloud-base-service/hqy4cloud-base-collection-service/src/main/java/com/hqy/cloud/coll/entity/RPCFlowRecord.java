package com.hqy.cloud.coll.entity;

import com.hqy.cloud.tk.model.BaseEntity;
import com.hqy.rpc.thrift.struct.ThriftRpcFlowStruct;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表t_rpc_minute_flow_record
 * @author qiyuan.hong
 * @date 2022-03-17 21:20
 */
@Data
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
    @Column(name = "`interval`")
    private Long interval;

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
        super(new Date());
        this.caller = struct.caller;
        this.provider = struct.provider;
        this.success = struct.success;
        this.failure = struct.failure;
        this.interval = struct.interval;
        this.total = struct.total;
        this.methodDetail = struct.methodDetail;
        this.serviceDetail = struct.serviceDetail;
    }

}

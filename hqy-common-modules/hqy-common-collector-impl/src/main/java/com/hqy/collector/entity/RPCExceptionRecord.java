package com.hqy.collector.entity;

import com.hqy.base.BaseEntity;
import com.hqy.rpc.thrift.struct.ThriftRpcExceptionStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * rpc异常记录表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/5 15:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_rpc_exception_record")
public class RPCExceptionRecord extends BaseEntity<Long> {
    private static final long serialVersionUID = -2951875856244763370L;

    /**
     * rpc类型 normal/slow/error
     */
    private String type;

    /**
     * provider application name.
     */
    private String application;

    /**
     * rpc接口名
     */
    private String serviceClassName;

    /**
     * rpc方法
     */
    private String method;

    /**
     * 请求时间戳
     */
    @Column(name = "request_time")
    private Long requestTime;

    /**
     * 耗时
     */
    private Long elapsed;

    /**
     * 错误消息
     */
    private String message;


    public RPCExceptionRecord(ThriftRpcExceptionStruct struct) {
        super(new Date());
        this.type = struct.type;
        this.method = struct.method;
        this.application = struct.application;
        this.elapsed = struct.elapsed;
        this.requestTime = struct.requestTime;
        this.serviceClassName = struct.serviceClassName;
        this.message = struct.message;
    }
}

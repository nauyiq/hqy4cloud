package com.hqy.cloud.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:27
 */
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class RpcFlowRecordStruct {

    /**
     * id
     */
    @ThriftField(1)
    public Long id;

    /**
     * 调用者
     */
    @ThriftField(2)
    public String caller;

    /**
     * rpc server.
     */
    @ThriftField(3)
    public String provider;

    /**
     * 调用的总次数
     */
    @ThriftField(4)
    public Integer total;

    /**
     * success count.
     */
    @ThriftField(5)
    public Integer success;

    /**
     * failed count.
     */
    @ThriftField(6)
    public Integer failure;

    /**
     * collection interval.
     */
    @ThriftField(7)
    public Long interval;

    /**
     * 接口分组的计数map
     */
    @ThriftField(8)
    public String serviceDetail;

    /**
     * 方法分组的计数map
     */
    @ThriftField(9)
    public String methodDetail;

    /**
     * 创建时间
     */
    @ThriftField(10)
    public String created;


}

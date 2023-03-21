package com.hqy.cloud.id.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取分布式id， thrift rpc返回结果
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 14:40
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class ResultStruct {

    /**
     * id
     */
    @ThriftField(1)
    public long id;

    /**
     * 是否请求成功.
     */
    @ThriftField(2)
    public boolean result;


}
